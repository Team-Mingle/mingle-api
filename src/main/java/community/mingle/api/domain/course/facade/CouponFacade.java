package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateCouponRequest;
import community.mingle.api.domain.course.controller.response.CouponProductListResponse;
import community.mingle.api.domain.course.controller.response.CouponProductResponse;
import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.service.CouponService;
import community.mingle.api.domain.course.service.CouponProductService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.global.amplitude.AmplitudeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final AmplitudeService amplitudeService;
    private final CouponProductService couponProductService;

    @Transactional
    public void create(CreateCouponRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        couponService.create(member, request.couponType());
        amplitudeService.log(memberId, "createCoupon", Map.of("couponType", request.couponType().toString()));
    }

    public CouponProductListResponse getCouponProductList() {
        List<CouponProduct> couponProductList = couponProductService.getAll();
        List<CouponProductResponse> couponProductResponse = couponProductList
                .stream().map(couponProduct ->
                new CouponProductResponse(
                        couponProduct.getId(),
                        couponProduct.getName(),
                        couponProduct.getDescription(),
                        couponProduct.getCost()
                )).toList();

        return new CouponProductListResponse(couponProductResponse);
    }

}
