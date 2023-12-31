package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Coupon;
import community.mingle.api.domain.point.entity.Point;
import community.mingle.api.domain.course.repository.CouponRepository;
import community.mingle.api.domain.point.entity.PointLog;
import community.mingle.api.domain.point.repository.PointLogRepository;
import community.mingle.api.domain.point.repository.PointRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.enums.CouponType;
import community.mingle.api.enums.PointEarningType;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static community.mingle.api.global.exception.ErrorCode.COUPON_TYPE_NOT_FOUND;
import static community.mingle.api.global.exception.ErrorCode.POINT_NOT_ENOUGH;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    private static final Long MONTHLY_COUPON_AMOUNT = 300L;
    private static final Long YEARLY_COUPON_AMOUNT = 1000L;

    @Transactional
    public Coupon create(Member member, CouponType couponType) {

        usePoint(member, couponType);
        Optional<Coupon> coupon = couponRepository.findByMemberId(member.getId());
        if (coupon.isPresent()) {
            return updateCoupon(couponType, coupon.get());
        } else {
            LocalDateTime expiresAt = determineExpiresAt(LocalDateTime.now() ,couponType);
            return couponRepository.save(
                    Coupon.builder()
                            .member(member)
                            .type(couponType)
                            .expiresAt(expiresAt)
                            .build()
            );
        }
    }

    @Transactional
    public Coupon updateCoupon(CouponType couponType, Coupon coupon) {
        LocalDateTime expiresAt = determineExpiresAt(coupon.getExpiresAt(), couponType);
        return coupon.updateExpiresAt(expiresAt);
    }

    private LocalDateTime determineExpiresAt(LocalDateTime baseExpiryDate ,CouponType couponType) {
        switch (couponType) {
            case MONTHLY -> {
                return baseExpiryDate.plusDays(30);
            }
            case YEARLY -> {
                return baseExpiryDate.plusYears(1);
            }
            case FRESHMAN -> {
                return baseExpiryDate
                        .withMonth(12)
                        .withDayOfMonth(31);
            }
            default -> throw new CustomException(COUPON_TYPE_NOT_FOUND);
        }
    }

    //TODO 추후에 point 사용처가 2개 이상 생기면 event 발생 구조로 리펙토링
    private void usePoint(Member member, CouponType couponType) {
        Point point = pointRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(POINT_NOT_ENOUGH));
        switch (couponType) {
            case MONTHLY -> {
                point.useAmount(MONTHLY_COUPON_AMOUNT);
                pointLogRepository.save(
                        PointLog.builder()
                                .memberId(member.getId())
                                .changedAmount(-MONTHLY_COUPON_AMOUNT)
                                .reason("BUY_MONTHLY_COUPON")
                                .build()
                );
            }
            case YEARLY -> {
                point.useAmount(YEARLY_COUPON_AMOUNT);
                pointLogRepository.save(
                        PointLog.builder()
                                .memberId(member.getId())
                                .changedAmount(-YEARLY_COUPON_AMOUNT)
                                .reason("BUY_YEARLY_COUPON")
                                .build()
                );
            }
            default -> {}
        }
    }
}
