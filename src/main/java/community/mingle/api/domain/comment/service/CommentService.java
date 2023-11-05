package community.mingle.api.domain.comment.service;


import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static community.mingle.api.enums.ContentStatusType.*;
import static community.mingle.api.global.exception.ErrorCode.COMMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        commentRepository.deleteAll(comments);
    }


    public List<Comment> findComments(Long postId, Long memberIdByJwt) {
        return commentRepository.findComments(postId, memberIdByJwt);
    }

    public List<Comment> findCoComments(Long postId, Long memberIdByJwt) {
        return commentRepository.findCoComments(postId, memberIdByJwt);
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

}
