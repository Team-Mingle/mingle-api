package community.mingle.api.domain.comment.controller.response;

import lombok.Builder;

@Builder
public record DeleteCommentLikeResponse(
        Boolean created
) {
}
