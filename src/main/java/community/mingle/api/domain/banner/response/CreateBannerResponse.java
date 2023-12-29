package community.mingle.api.domain.banner.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public class CreateBannerResponse {

    private List<String> imgUrls;
}
