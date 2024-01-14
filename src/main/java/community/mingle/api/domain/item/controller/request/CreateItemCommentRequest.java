package community.mingle.api.domain.item.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemCommentRequest(
        @NotNull(message = "itemId를 입력해주세요")
        Long itemId,
        Long parentCommentId,
        Long mentionId,
        @NotBlank(message = "댓글을 입력해주세요")
        String content,
        boolean isAnonymous
) {
}