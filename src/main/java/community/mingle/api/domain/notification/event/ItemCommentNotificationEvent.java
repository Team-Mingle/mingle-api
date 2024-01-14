package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.facade.ItemCommentFacade;
import community.mingle.api.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ItemCommentNotificationEvent extends ApplicationEvent {

    private final Item item;
    private final ItemComment itemComment;
    private final Member member;
    private final Long parentCommentId;
    private final Long mentionId;
    private final String content;

    public ItemCommentNotificationEvent(
            Object source,
            Item item,
            ItemComment itemComment,
            Member member,
            Long parentCommentId,
            Long mentionId,
            String content
    ) {
        super(source);
        this.item = item;
        this.member = member;
        this.itemComment = itemComment;
        this.parentCommentId = parentCommentId;
        this.mentionId = mentionId;
        this.content = content;
    }
}
