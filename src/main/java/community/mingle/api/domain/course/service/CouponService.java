package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Coupon;
import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.repository.CouponRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.notification.event.FreshmanCouponNotificationEvent;
import community.mingle.api.domain.notification.event.TempSignupNotificationEvent;
import community.mingle.api.domain.point.entity.Point;
import community.mingle.api.domain.point.entity.PointLog;
import community.mingle.api.domain.point.repository.PointLogRepository;
import community.mingle.api.domain.point.repository.PointRepository;
import community.mingle.api.enums.MemberAuthPhotoStatus;
import community.mingle.api.enums.MemberAuthPhotoType;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static community.mingle.api.global.exception.ErrorCode.POINT_NOT_ENOUGH;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Optional<Coupon> getByMember(Member member) {
        LocalDateTime currentDateTimeInKst = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        List<Coupon> couponsMayContainsExpired = couponRepository.findByMemberId(member.getId());
        couponsMayContainsExpired.forEach( coupon -> {
                if (coupon.getExpiresAt().isBefore(currentDateTimeInKst)) {
                    couponRepository.delete(coupon);
                }
            }
        );
        for (Coupon coupon : couponsMayContainsExpired) {
            if (!coupon.getExpiresAt().isBefore(currentDateTimeInKst)) {
                return Optional.of(coupon);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Coupon create(Member member, CouponProduct couponProduct) {

        usePoint(member, couponProduct.getCost(), couponProduct.getName());
        LocalDateTime currentDateTimeInKst = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        Optional<Coupon> coupon = getByMember(member);

        if (coupon.isPresent()) {
            return updateCoupon(coupon.get(), couponProduct.getDurationInDay());
        } else {
            LocalDateTime expiresAt = currentDateTimeInKst.plusDays(couponProduct.getDurationInDay());
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

    @Transactional
    public Coupon createFreshmanCoupon(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Optional<Coupon> coupon = getByMember(member);
        if (coupon.isPresent()) {
            return updateCoupon(coupon.get(), 120);
        } else {
            LocalDateTime currentDateTimeInKst = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
            LocalDateTime expiresAt = currentDateTimeInKst.plusDays(120);
            return couponRepository.save(
                    Coupon.builder()
                            .member(member)
                            .expiresAt(expiresAt)
                            .build()
            );
        }
    }

    public void sendCreateFreshmanCouponNotification(String fcmToken, MemberAuthPhotoStatus status, @Nullable  String rejectReason) {
        String title = switch (status) {
            case ACCEPTED -> "새내기 강의평가 이용권이 발급되었어요!";
            case REJECTED -> "새내기 강의평가 이용권 발급에 실패했어요.";
            default -> null;
        };

        String content = switch (status) {
            case ACCEPTED -> "4달 동안 자유롭게 강의평가를 열람할 수 있어요.";
            case REJECTED -> rejectReason;
            default -> null;
        };
        applicationEventPublisher.publishEvent(
                new FreshmanCouponNotificationEvent(this, fcmToken , title, content)
        );
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
