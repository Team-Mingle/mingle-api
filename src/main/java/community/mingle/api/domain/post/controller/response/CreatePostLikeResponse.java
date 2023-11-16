package community.mingle.api.domain.post.controller.response;

import lombok.Builder;

@Builder
public record CreatePostLikeResponse(
        Boolean created
) {
}
