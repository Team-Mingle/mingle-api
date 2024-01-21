package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;


/**
 *  Base Notification entity
 *  all subclasses must implement NotificationContentProvider to provide appropriate Notification content by each content type
 */
@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE notification SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "notification")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "content_type")
public class Notification extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "content_type", updatable = false) //TODO content_id와 함께 redirect용이라는걸 표현하는 naming으로 변경
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @NotNull
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @NotNull
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @NotNull
    @Column(name = "`read`", nullable = false)
    private Boolean read = false;

    public void markAsRead() {
        this.read = true;
    }

    //Notification 생성 로직 응집도를 위해 static method로 분리
    public static PostNotification createPostNotification(Post post, NotificationType type, Member member) {
        return PostNotification.builder()
                .post(post)
                .member(member)
                .notificationType(type) // POPULAR, POST
                .contentId(post.getId())
                .contentType(ContentType.POST)
                .read(false)
                .build();
    }

    public static CommentNotification createCommentNotification(Comment comment, Post post, Member member) {
        return CommentNotification.builder()
                .comment(comment)
                .member(member)
                .notificationType(NotificationType.COMMENT)
                .contentId(post.getId())
                .contentType(ContentType.POST)
                .read(false)
                .build();
    }

    public static ItemCommentNotification createItemCommentNotification(ItemComment comment, Item item, Member member) {
        return ItemCommentNotification.builder()
                .itemComment(comment)
                .member(member)
                .notificationType(NotificationType.COMMENT)
                .contentId(item.getId())
                .contentType(ContentType.ITEM)
                .read(false)
                .build();
    }

}