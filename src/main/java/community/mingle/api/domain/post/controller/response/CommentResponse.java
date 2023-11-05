package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CommentResponse {

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
    private final List<CoCommentDto> coCommentsList;
}
