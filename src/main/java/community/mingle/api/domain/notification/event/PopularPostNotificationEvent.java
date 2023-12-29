package community.mingle.api.domain.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PopularPostNotificationEvent extends ApplicationEvent {

    private final Long postId;
    private final Long memberId;


    public PopularPostNotificationEvent(Object source, Long postId, Long memberId) {
        super(source);
        this.postId = postId;
        this.memberId = memberId;
    }
}
