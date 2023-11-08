package community.mingle.api.domain.comment.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCommentResponse {
    private final Long commentId;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final Boolean isCommentFromAuthor;
}
