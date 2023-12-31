package community.mingle.api.domain.point.event.eventhandler;

import community.mingle.api.domain.point.entity.Point;
import community.mingle.api.domain.point.entity.PointLog;
import community.mingle.api.domain.point.repository.PointLogRepository;
import community.mingle.api.domain.point.repository.PointRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.point.event.EarningPointEvent;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class PointEventHandler {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
    private final PointLogRepository pointLogRepository;

    @EventListener(EarningPointEvent.class)
    @Transactional
    public void handleEarningPointEvent(EarningPointEvent event) {
        pointRepository.findById(event.getMemberId())
                .ifPresentOrElse(
                        point -> point.addAmount(event.getPointEarningType().getEarningAmount()),

                        () -> {
                            Member member = memberRepository.findById(event.getMemberId())
                                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
                            Point point = Point.builder()
                                    .member(member)
                                    .amount(event.getPointEarningType().getEarningAmount())
                                    .build();
                            pointRepository.save(point);
                        }
                );
        pointLogRepository.save(
                PointLog.builder()
                        .memberId(event.getMemberId())
                        .changedAmount(event.getPointEarningType().getEarningAmount())
                        .reason(event.getPointEarningType().getReason())
                        .build()
        );
    }
}
