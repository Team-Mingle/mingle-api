package community.mingle.api.domain.item.controller.response;

import community.mingle.api.enums.CurrencyType;

import java.util.List;

public record ItemResponse(

        Long itemId,
        String title,
        String content,
        String price,
        CurrencyType currency,
        String location,
        String chatUrl,
        String nickname,
        String boardType,
        String categoryType,
        String memberRole,
        int likeCount,
        int commentCount,
        int viewCount,
        List<String> itemImgUrl,
        boolean isFileAttached,
        boolean isAnonymous,
        boolean isMyPost,
        boolean isLiked,
        boolean isReported,
        boolean isBlinded,
        String createdAt
) {
}
