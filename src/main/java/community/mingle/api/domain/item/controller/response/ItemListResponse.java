package community.mingle.api.domain.item.controller.response;

import community.mingle.api.dto.item.ItemPreviewDto;

import java.util.List;

public record ItemListResponse(
        List<ItemPreviewDto> data
) {

}
