package community.mingle.api.dto.item;

import community.mingle.api.enums.CurrencyType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ItemPreviewDto {

    private final Long id;
    private final String title;
    private final String content;
    private final Long price;
    private String nickName;
    private final String createdAt;
    private final int likeCount;
    private final int commentCount;
    private final String status;
    private final String imgThumbnailUrl;
    private final String location;
    private final List<String> itemImgList;
    private final String chatUrl;
    private final boolean isLiked;
    private final CurrencyType currency;

}
