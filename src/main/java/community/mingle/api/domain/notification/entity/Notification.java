package community.mingle.api.domain.notification.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE notification SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "notification")
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
    @Column(name = "board", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @NotNull
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @NotNull
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @NotNull
    @Column(name = "`read`", nullable = false)
    private Boolean read = false;

}