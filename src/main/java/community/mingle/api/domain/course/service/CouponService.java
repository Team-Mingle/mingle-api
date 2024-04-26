package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Coupon;
import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.repository.CouponRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.point.entity.Point;
import community.mingle.api.domain.point.entity.PointLog;
import community.mingle.api.domain.point.repository.PointLogRepository;
import community.mingle.api.domain.point.repository.PointRepository;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static community.mingle.api.global.exception.ErrorCode.COURSE_NOT_FOUND;
import static community.mingle.api.global.exception.ErrorCode.POINT_NOT_ENOUGH;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    public Coupon getByMember(Member member) {
        return couponRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
    }

    @Transactional
    public Coupon create(Member member, CouponProduct couponProduct) {

        usePoint(member, couponProduct.getCost(), couponProduct.getName());
        Optional<Coupon> coupon = couponRepository.findByMemberId(member.getId());
        if (coupon.isPresent()) {
            return updateCoupon(coupon.get(), couponProduct.getDurationInDay());
        } else {
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(couponProduct.getDurationInDay());
            return couponRepository.save(
                    Coupon.builder()
                            .member(member)
                            .expiresAt(expiresAt)
                            .build()
            );
        }
    }

    @Transactional
    public Coupon updateCoupon(Coupon coupon, Integer extendDays) {
        LocalDateTime expiresAt = coupon.getExpiresAt().plusDays(extendDays);
        return coupon.updateExpiresAt(expiresAt);
    }

    private void usePoint(Member member, Long cost, String productName) {
        Point point = pointRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(POINT_NOT_ENOUGH));

        point.useAmount(cost);
        pointLogRepository.save(
                PointLog.builder()
                        .memberId(member.getId())
                        .changedAmount(-cost)
                        .reason("Purchase" + productName)
                        .build()
        );
    }
}
