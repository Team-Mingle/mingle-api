package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.backoffice.controller.response.FreshmanCouponApplyListResponse;
import community.mingle.api.domain.course.controller.request.CreateCouponForFreshmanRequest;
import community.mingle.api.domain.course.controller.request.CreateCouponRequest;
import community.mingle.api.domain.course.controller.response.CouponProductListResponse;
import community.mingle.api.domain.course.controller.response.CouponProductResponse;
import community.mingle.api.domain.course.controller.response.CouponResponse;
import community.mingle.api.domain.course.entity.Coupon;
import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.service.CouponService;
import community.mingle.api.domain.course.service.CouponProductService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import community.mingle.api.domain.member.service.MemberAuthPhotoService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.enums.MemberAuthPhotoStatus;
import community.mingle.api.enums.MemberAuthPhotoType;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.amplitude.AmplitudeService;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.global.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static community.mingle.api.global.exception.ErrorCode.MODIFY_NOT_AUTHORIZED;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final AmplitudeService amplitudeService;
    private final CouponProductService couponProductService;
    private final S3Service s3Service;
    private final MemberAuthPhotoService memberAuthPhotoService;

    @Transactional
    public void create(CreateCouponRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        CouponProduct couponProduct = couponProductService.getById(request.couponProductId());
        couponService.create(member, couponProduct);
        amplitudeService.log(memberId, "createCoupon", Map.of("couponTypeId", request.couponProductId().toString()));
    }

    public CouponResponse getCoupon() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Optional<Coupon> coupon = couponService.getByMember(member);

        return coupon.map(value -> new CouponResponse(
                "강의평가 조회 이용권",
                value.getCreatedAt().toLocalDate(),
                value.getExpiresAt().toLocalDate()
        )).orElse(null);
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

    @Transactional
    public void createCouponRequestForFreshman(CreateCouponForFreshmanRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<MemberAuthPhoto> acceptedRequest = memberAuthPhotoService.getAuthenticatedFreshmanCouponRequestPhotoList(memberId);
        if (!acceptedRequest.isEmpty()) {
            throw new CustomException(ErrorCode.FRESHMAN_COUPON_ALREADY_GIVEN);
        }
        List<String> imgUrls = s3Service.uploadFile(request.multipartFile(), "temp_auth");
        imgUrls.forEach(imgUrl ->
                memberAuthPhotoService.create(memberId, imgUrl, MemberAuthPhotoType.FRESHMAN_COUPON)
        );
    }

    public FreshmanCouponApplyListResponse getFreshmanCouponApplyList() {
        List<MemberAuthPhoto> photoList = memberAuthPhotoService.getUnauthenticatedFreshmanCouponRequestPhotoList();
        List<FreshmanCouponApplyListResponse.FreshmanCouponApplyResponse> responses = photoList.stream().map(photo ->
                        new FreshmanCouponApplyListResponse.FreshmanCouponApplyResponse(
                                photo.getId(),
                                photo.getImageUrl()
                        ))
                .toList();
        return new FreshmanCouponApplyListResponse(responses);
    }

    @Transactional
    public void authenticateFreshmanCoupon(Long memberId) {
        if (!tokenService.getTokenInfo().getMemberRole().equals(MemberRole.ADMIN)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        Member member = memberService.getById(memberId);
        memberAuthPhotoService.getById(memberId, MemberAuthPhotoType.FRESHMAN_COUPON).accepted();
        couponService.createFreshmanCoupon(memberId);
        couponService.sendCreateFreshmanCouponNotification(member.getFcmToken(), MemberAuthPhotoStatus.ACCEPTED, null);

    }

    @Transactional
    public void rejectFreshmanCoupon(Long memberId, String reason) {
        if (!tokenService.getTokenInfo().getMemberRole().equals(MemberRole.ADMIN)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        Member member = memberService.getById(memberId);
        memberAuthPhotoService.getById(memberId, MemberAuthPhotoType.FRESHMAN_COUPON).rejected();
        couponService.sendCreateFreshmanCouponNotification(member.getFcmToken(), MemberAuthPhotoStatus.REJECTED, reason);
    }
}
