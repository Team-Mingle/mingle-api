package community.mingle.api.domain.like.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE content_like SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "content_like")
public class ContentLike extends AuditLoggingBase {
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
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
}