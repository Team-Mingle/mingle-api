package community.mingle.api.domain.item.controller.response;

import lombok.Builder;

@Builder
public record DeleteItemPostResponse(
        boolean deleted
) {
}



