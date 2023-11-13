package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CommentDto {

    private final Long commentId;
    private final String nickname;
    private final String content;
    private final int likeCount;
    private final boolean isLiked;
    private final boolean isMyComment;
    private final boolean isCommentFromAuthor;
    private final boolean isCommentDeleted;
    private final boolean isCommentReported;
    private final String createdAt;
    private final boolean isAdmin;
}
