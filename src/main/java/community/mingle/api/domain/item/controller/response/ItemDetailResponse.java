package community.mingle.api.domain.item.controller.response;

import community.mingle.api.enums.CurrencyType;
import community.mingle.api.enums.ItemStatusType;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemDetailResponse(

        Long id,
        String title,
        String content,
        Long price,
        CurrencyType currency,
        String location,
        String chatUrl,
        String nickname,
        int likeCount,
        int commentCount,
        int viewCount,
        String imgThumbnailUrl,
        List<String> itemImgList,

        boolean isFileAttached,
        boolean isAnonymous,
        boolean isMyPost,
        boolean isLiked,
        boolean isReported,
        boolean isBlinded,
        boolean isAdmin,
        String status,
        String createdAt
) {
}
