package community.mingle.api.domain.comment.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotNull
    private final Long postId;
    private final Long parentCommentId;
    private final Long mentionId;
    @NotEmpty
    private final String content;
    private final boolean isAnonymous;
}
