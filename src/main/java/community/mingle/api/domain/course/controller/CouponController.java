package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreateCouponRequest;
import community.mingle.api.domain.course.facade.CouponFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
