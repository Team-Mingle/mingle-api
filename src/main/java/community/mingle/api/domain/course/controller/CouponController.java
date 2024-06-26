package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreateCouponForFreshmanRequest;
import community.mingle.api.domain.course.controller.request.CreateCouponRequest;
import community.mingle.api.domain.course.controller.response.CouponProductListResponse;
import community.mingle.api.domain.course.controller.response.CouponResponse;
import community.mingle.api.domain.course.facade.CouponFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Coupon Controller", description = "이용권 관련 API")
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "이용권 생성 API")
    @PostMapping("/create")
    public ResponseEntity<Void> createCoupon(
            @RequestBody
            CreateCouponRequest request
    ) {
        couponFacade.create(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이용권 종류 리스트 조회 API")
    @GetMapping("/shop")
    public ResponseEntity<CouponProductListResponse> getCouponShopList() {
        CouponProductListResponse couponProductList = couponFacade.getCouponProductList();
        return ResponseEntity.ok().body(couponProductList);
    }

    @Operation(summary = "보유 중인 이용권 조회 API", description = "유저가 보유 중인 이용권을 조회합니다. 보유 중인 이용권이 없으면 반환 값이 없습니다.")
    @GetMapping()
    public ResponseEntity<CouponResponse> getCoupon() {
        CouponResponse response = couponFacade.getCoupon();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "새내기 이용권 요청 API")
    @PostMapping(path = "/create/freshman", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> couponRequestForFreshman(
        @ModelAttribute
        @Valid
        CreateCouponForFreshmanRequest request
    ) {
        couponFacade.createCouponRequestForFreshman(request);
        return ResponseEntity.ok().build();
    }
}
