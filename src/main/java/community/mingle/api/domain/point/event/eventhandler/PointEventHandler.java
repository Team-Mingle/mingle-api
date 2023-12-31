package community.mingle.api.domain.point.event.eventhandler;

import community.mingle.api.domain.course.entity.Point;
import community.mingle.api.domain.course.repository.PointRepository;
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
    @EventListener(EarningPointEvent.class)
    @Async
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
    }
}
