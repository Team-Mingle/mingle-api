package community.mingle.api.domain.comment.service;


import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.repository.CommentQueryRepository;
import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.*;
import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Comment create(
            Long memberId,
            Long postId,
            Long parentCommentId,
            Long mentionId,
            String content,
            boolean isAnonymous
    ) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(POST_NOT_EXIST));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        checkValidity(post, parentCommentId, mentionId);
        Long anonymousId;
        if (isAnonymous) {
            anonymousId = calculateAnonymousId(post, member);
        } else anonymousId = 0L;

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .parentCommentId(parentCommentId)
                .mentionId(mentionId)
                .content(content)
                .anonymous(isAnonymous)
                .statusType(ACTIVE)
                .anonymousId(anonymousId)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        commentRepository.deleteAll(comments);
    }


    public List<Comment> findComments(Long postId, Long memberId) {
        return commentQueryRepository.findComments(postId, memberId, false);
    }

    public List<Comment> findCoComments(Long postId, Long memberId) {
        return commentQueryRepository.findComments(postId, memberId, true);
    }

    public Map<Comment, List<Comment>> getCommentsWithCoCommentsMap(Long postId, Long memberIdByJwt) {
        List<Comment> allComments = findComments(postId, memberIdByJwt);
        List<Comment> allCoComments = findCoComments(postId, memberIdByJwt);

        Map<Comment, List<Comment>> commentListMap = new HashMap<>();

        for (Comment c : allComments) {
            List<Comment> coCommentList = allCoComments.stream()
                    .filter(cc -> c.getId().equals(cc.getParentCommentId()))
                    .filter(cc -> !cc.getStatusType().equals(DELETED))
                    .toList();
            if (c.getStatusType().equals(INACTIVE) && coCommentList.isEmpty()) continue;
            commentListMap.put(c, coCommentList);
        }
        return commentListMap;
    }

    public String getContentByStatus(Comment comment) {
        return switch (comment.getStatusType()) {
            case INACTIVE -> "삭제된 댓글입니다.";
            case DELETED -> "운영규칙 위반에 따라 삭제된 댓글입니다.";
            case REPORTED -> "신고된 댓글입니다.";
            default -> comment.getContent();
        };
    }

    public String getMentionDisplayName(Long mentionId, Long postAuthorId) {
        Comment comment = commentRepository.findById(mentionId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        return getDisplayName(comment, postAuthorId);
    }

    public String getDisplayName(Comment comment, Long postAuthorId) {
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
        if (comment.getStatusType() == REPORTED || comment.getStatusType() == DELETED) {
            displayName = "(비공개됨)";
        }
        return displayName;
    }

    public boolean isCommentLikedByMember(Comment comment, Long memberId) {
        return comment.getCommentLikes().stream()
                .anyMatch(c -> c.getMember().getId().equals(memberId));
    }

    private void checkValidity(Post post, Long parentCommentId, Long mentionCommentId) {
        Set<Long> commentIdList = post.getCommentList().stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
        if (parentCommentId != null) {
            if (!commentIdList.contains(parentCommentId)) {
                throw new CustomException(FAIL_TO_CREATE_COMMENT);
            }
        }
        if (mentionCommentId != null) {
            if (!commentIdList.contains(mentionCommentId)) {
                throw new CustomException(FAIL_TO_CREATE_COMMENT);
            }
        }
    }
    private Long calculateAnonymousId(Post post, Member member) {
        Optional<List<Comment>> comments = commentRepository.findAllByPostId(post.getId());

        //해당 게시글에 댓글이 없을 경우
        if (comments.isEmpty()) {
            return 1L;
        }

        Optional<Comment> resultComment = comments.flatMap(commentList -> commentList.stream()
                .filter(comment -> comment.getMember().equals(member) && comment.getAnonymous())
                .findFirst());

        //해당 게시글에 유저가 익명으로 댓글을 쓴 적이 있을 경우
        if (resultComment.isPresent()) {
            return resultComment.get().getAnonymousId();
        }
        //해당 게시글에 유저가 익명으로 댓글을 처음쓰는 경우
        else return comments.get().stream()
                .map(Comment::getAnonymousId)
                .max(Long::compareTo)
                .orElse(1L);
    }


}
