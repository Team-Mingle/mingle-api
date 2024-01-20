package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.item.entity.ItemComment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static community.mingle.api.enums.BoardType.ITEM;
import static io.netty.util.internal.StringUtil.EMPTY_STRING;


@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "item_comment_notification")
public class ItemCommentNotification extends Notification implements NotificationContentProvider{


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_comment_id", nullable = false)
    private ItemComment itemComment;

    @Override
    public String getContent() {
        return itemComment.getContent();
    }

    @Override
    public String getBoardType() {
        return ITEM.getBoardName();
    }

    @Override
    public String getCategoryType() {
        return EMPTY_STRING;
    }
}
