package community.mingle.api.domain.point.event;

import community.mingle.api.enums.PointEarningType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EarningPointEvent extends ApplicationEvent {

    private final PointEarningType pointEarningType;
    private final Long memberId;
    public EarningPointEvent(Object source, PointEarningType pointEarningType, Long memberId) {
        super(source);
        this.pointEarningType = pointEarningType;
        this.memberId = memberId;
    }

}
