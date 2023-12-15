package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "post_notification")
public class PostNotification extends Notification implements NotificationContentProvider{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Override
    public String getContent() {
        return post.getTitle();
    }
}
