package community.mingle.api.domain.banner;

import community.mingle.api.domain.banner.request.CreateBannerRequest;
import community.mingle.api.domain.banner.response.BannerResponse;
import community.mingle.api.domain.banner.response.CreateBannerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Banner Controller", description = "배너 관련 API")
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerFacade bannerFacade;

    @GetMapping()
    @Operation(summary = "5.1 홈 화면 배너 리스트 조회 API")
    public ResponseEntity<List<BannerResponse>> getBanner() {
        return new ResponseEntity<>(bannerFacade.getBanner(), HttpStatus.OK);
    }

    @Operation(summary = "5.2 배너 사진 업로드 API")
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateBannerResponse> uploadBanner(@ModelAttribute @Valid CreateBannerRequest request) {
        return new ResponseEntity<>(bannerFacade.createBanner(request.getMultipartFile(), request.getLinkUrl(), request.getUniversityId()), HttpStatus.OK);
    }

}
