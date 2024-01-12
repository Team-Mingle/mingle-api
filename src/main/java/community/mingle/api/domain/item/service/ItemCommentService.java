package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.repository.ItemCommentQueryRepository;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static community.mingle.api.enums.ContentStatusType.*;
import static community.mingle.api.global.exception.ErrorCode.COMMENT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemCommentService {

    private final ItemCommentRepository itemCommentRepository;
    private final ItemCommentQueryRepository itemCommentQueryRepository;

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
}

