package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.comment.entity.Comment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "comment_notification")
public class CommentNotification extends Notification implements NotificationContentProvider {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Override
    public String getContent() {
        return comment.getContent();
    }

    @Override
    public String getBoardType() {
        return comment.getPost().getBoardType().getBoardName();
    }

    @Override
    public String getCategoryType() {
        return comment.getPost().getCategoryType().getCategoryName();
    }

}