package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ItemCommentNotificationEvent extends ApplicationEvent {

    private final Long itemId;
    private final Long itemCommentId;
    private final Long memberId;
    private final Long parentCommentId;
    private final Long mentionId;
    private final String content;

    public ItemCommentNotificationEvent(
            Object source,
            Long itemId,
            Long itemCommentId,
            Long memberId,
            Long parentCommentId,
            Long mentionId,
            String content
    ) {
        super(source);
        this.itemId = itemId;
        this.memberId = memberId;
        this.itemCommentId = itemCommentId;
        this.parentCommentId = parentCommentId;
        this.mentionId = mentionId;
        this.content = content;
    }
}
