package community.mingle.api.domain.member.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.enums.MemberStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "member")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Member extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Size(max = 45)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 45)
    private String nickname;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 100)
    @NotNull
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "fcm_token")
    private String fcmToken;

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}