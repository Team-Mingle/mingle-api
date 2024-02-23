package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.repository.ItemCommentQueryRepository;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.domain.item.repository.ItemRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.*;
import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemCommentService {

    private final ItemCommentRepository itemCommentRepository;
    private final ItemCommentQueryRepository itemCommentQueryRepository;
    private final ItemRepository itemRepository;

    public Map<ItemComment, List<ItemComment>> getCommentsWithCoCommentsMap(Long itemId, Long memberId) {
        List<ItemComment> allComments = findComments(itemId, memberId);
        List<ItemComment> allCoComments = findCoComments(itemId, memberId);

        LinkedHashMap<ItemComment, List<ItemComment>> commentListMap = new LinkedHashMap<>();

        for (ItemComment c : allComments) {
            List<ItemComment> coCommentList = allCoComments.stream()
                    .filter(cc -> c.getId().equals(cc.getParentCommentId()))
                    .filter(cc -> !cc.getStatus().equals(DELETED))
                    .toList();
            if (c.getStatus().equals(INACTIVE) && coCommentList.isEmpty()) continue;
            commentListMap.put(c, coCommentList);
        }
        return commentListMap;

    }

    public List<ItemComment> findComments(Long itemId, Long memberId) {
        return itemCommentQueryRepository.findComments(itemId, memberId, false);
    }

    public List<ItemComment> findCoComments(Long itemId, Long memberId) {
        return itemCommentQueryRepository.findComments(itemId, memberId, true);
    }


    public String getDisplayName(ItemComment comment, Long postAuthorId) {
        String displayName;
        boolean isAnonymous = comment.getAnonymous();
        Long commentWriterId = comment.getMember().getId();
        Long anonymousId = comment.getAnonymousId();

        if (!isAnonymous && !Objects.equals(commentWriterId, postAuthorId)) {
            displayName = comment.getMember().getNickname();
        } else if (isAnonymous && anonymousId != null && anonymousId != 0L) {
            displayName = "익명 " + anonymousId;
        } else if (!isAnonymous) {
            displayName = comment.getMember().getNickname() + "(글쓴이)";
        } else {
            displayName = "익명(글쓴이)";
        }
        if (comment.getStatus() == REPORTED || comment.getStatus() == DELETED) {
            displayName = "(비공개됨)";
        }
        return displayName;
    }

    public String getContentByStatus(ItemComment comment) {
        return switch (comment.getStatus()) {
            case INACTIVE -> "삭제된 댓글입니다.";
            case DELETED -> "운영규칙 위반에 따라 삭제된 댓글입니다.";
            case REPORTED -> "신고된 댓글입니다.";
            default -> comment.getContent();
        };
    }

    public boolean isCommentLikedByMember(ItemComment comment, Long memberId) {
        return comment.getItemCommentLikes().stream()
                .anyMatch(c -> c.getMember().getId().equals(memberId));
    }

    public String getMentionDisplayName(Long mentionId, Long postAuthorId) {
        ItemComment comment = itemCommentRepository.findById(mentionId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        return getDisplayName(comment, postAuthorId);
    }

    @Transactional
    public void deleteAllByItemId(Long itemId) {
        List<ItemComment> comments = itemCommentRepository.findAllByItemId(itemId);
        itemCommentRepository.deleteAll(comments);
    }

    @Transactional
    public ItemComment createComment(
            Item item,
            Member member,
            Long parentCommentId,
            Long mentionId,
            String content,
            boolean isAnonymous
    ) {
        checkValidity(item, parentCommentId, mentionId);

        Long anonymousId = 0L;
        if (isAnonymous) {
            anonymousId = calculateAnonymousId(item, member);
        }

        ItemComment itemComment = ItemComment.builder()
                .item(item)
                .member(member)
                .parentCommentId(parentCommentId)
                .mentionId(mentionId)
                .content(content)
                .anonymous(isAnonymous)
                .anonymousId(anonymousId)
                .status(ACTIVE)
                .build();
        return itemCommentRepository.save(itemComment);
    }


    private void checkValidity(Item item, Long parentCommentId, Long mentionCommentId) {
        Set<Long> commentIdList = item.getItemCommentList().stream()
                .map(ItemComment::getId)
                .collect(Collectors.toSet());

        Set<Long> commentIdListWithoutCoComment = item.getItemCommentList().stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .map(ItemComment::getId)
                .collect(Collectors.toSet());

        //parentCommentId가 해당 게시물의 댓글이 아니거나 parentCommentId가 대댓글일 경우 에러 (parentCommentId는 대댓글이 아닌 댓글이여야함)
        if (parentCommentId != null && !commentIdListWithoutCoComment.contains(parentCommentId)) {
            throw new CustomException(FAIL_TO_CREATE_COMMENT);
        }

        if (mentionCommentId != null) {
            if (!commentIdList.contains(mentionCommentId)) {
                throw new CustomException(FAIL_TO_CREATE_COMMENT);
            }
        }
    }

    private Long calculateAnonymousId(Item item, Member member) {
        List<ItemComment> commentList = itemCommentRepository.findAllByItemId(item.getId());

        //해당 게시글에 댓글이 없을 경우
        if (commentList.isEmpty()) {
            return 1L;
        }

        Optional<ItemComment> resultComment = commentList.stream()
                .filter(comment -> Objects.equals(comment.getMember().getId(), member.getId()) && comment.getAnonymous())
                .findFirst();

        //해당 게시글에 유저가 익명으로 댓글을 쓴 적이 있을 경우
        if (resultComment.isPresent()) {
            return resultComment.get().getAnonymousId();
        }
        //해당 게시글에 유저가 익명으로 댓글을 처음쓰는 경우
        else return commentList.stream()
                .map(ItemComment::getAnonymousId)
                .max(Long::compareTo)
                .orElse(1L) + 1L;
    }


    @Transactional
    public void delete(Long commentId, Long memberId) {
        ItemComment itemComment = itemCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        if (!itemComment.getMember().getId().equals(memberId)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        itemCommentRepository.delete(itemComment);
    }
}

