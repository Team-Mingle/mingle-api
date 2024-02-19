package community.mingle.api.domain.post.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReadPostEvent extends ApplicationEvent {
    private final Long postId;
    private final Long memberId;
    public ReadPostEvent(Object source, Long postId, Long memberId) {
        super(source);
        this.postId = postId;
        this.memberId = memberId;
    }
}
