package community.mingle.api.domain.notification.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FreshmanCouponNotificationEvent extends ApplicationEvent {
    private final String fcmToken;
    private final String title;
    private final String body;

    public FreshmanCouponNotificationEvent(Object source, @NotNull String fcmToken, @NotBlank String title, @NotBlank String body) {
        super(source);
        this.fcmToken = fcmToken;
        this.title = title;
        this.body = body;
    }
}
