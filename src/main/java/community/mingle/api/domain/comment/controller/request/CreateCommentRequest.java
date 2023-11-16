package community.mingle.api.domain.comment.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record CreateCommentRequest(
        @NotNull(message = "postId를 입력해주세요")
        Long postId,
        Long parentCommentId,
        Long mentionId,
        @NotBlank(message = "댓글을 입력해주세요")
        String content,
        boolean isAnonymous
) {
}
