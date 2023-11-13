package community.mingle.api.domain.banner.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerResponse {
    private int id;
    private String imgUrl;
    private String linkUrl;
}

