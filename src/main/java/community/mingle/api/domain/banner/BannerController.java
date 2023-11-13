package community.mingle.api.domain.banner;

import community.mingle.api.domain.banner.request.CreateBannerRequest;
import community.mingle.api.domain.banner.response.BannerResponse;
import community.mingle.api.domain.banner.response.CreateBannerResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerFacade bannerFacade;

    @GetMapping()
    @Operation(summary = "5.1 홈 화면 배너 리스트 조회 API")
    public ResponseEntity<List<BannerResponse>> getBanner() {
        return new ResponseEntity<>(bannerFacade.findBanner(), HttpStatus.OK);
    }

    @Operation(summary = "5.2 배너 사진 업로드 API")
    @PostMapping()
    public ResponseEntity<CreateBannerResponse> uploadBanner(@ModelAttribute CreateBannerRequest request) {
        return new ResponseEntity<>(bannerFacade.createBanner(request.getMultipartFile(), request.getLinkUrl()), HttpStatus.OK);
    }

}
