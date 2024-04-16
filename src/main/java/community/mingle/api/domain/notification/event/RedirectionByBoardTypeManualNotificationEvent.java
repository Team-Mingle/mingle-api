package community.mingle.api.domain.notification.event;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CountryType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RedirectionByBoardTypeManualNotificationEvent extends ApplicationEvent {
    private final BoardType boardType;
    private final String title;
    private final String body;
    private final CountryType countryType;


    public RedirectionByBoardTypeManualNotificationEvent(Object source, BoardType boardType, @NotBlank String title, @NotBlank String body, CountryType countryType) {
        super(source);
        this.boardType = boardType;
        this.title = title;
        this.body = body;
        this.countryType = countryType;
    }
}
