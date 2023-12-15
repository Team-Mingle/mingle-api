package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.member.entity.Member;
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
    @Column(name = "content_type", updatable = false)
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
}