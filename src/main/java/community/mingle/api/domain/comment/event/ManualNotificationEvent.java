package community.mingle.api.domain.comment.event;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ManualNotificationEvent extends ApplicationEvent {
    private final BoardType boardType;
    private final String title;
    private final String body;
    private final Long contentId;
    private final ContentType contentType;


    public ManualNotificationEvent(Object source, BoardType boardType, @NotBlank String title, @NotBlank String body, Long contentId, @NotNull ContentType contentType) {
        super(source);
        this.boardType = boardType;
        this.title = title;
        this.body = body;
        this.contentId = contentId;
        this.contentType = contentType;
    }
}
