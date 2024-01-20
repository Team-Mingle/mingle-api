package community.mingle.api.dto.item;

import community.mingle.api.enums.CurrencyType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

//@Getter
@Builder
public record ItemPreviewDto(

            Long id,
            String title,
            String content,
            Long price,
            String nickname,
            String createdAt,
            int likeCount,
            int commentCount,
            String status,
            String imgThumbnailUrl,
            String location,
            List<String> itemImgList,
            String chatUrl,
            boolean isLiked,
            CurrencyType currency
) {
}
