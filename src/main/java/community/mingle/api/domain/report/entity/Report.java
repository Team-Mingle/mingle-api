package community.mingle.api.domain.report.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.ReportType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE report SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "report")
public class Report extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_member_id", nullable = false)
    private Member reporterMember;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_member_id", nullable = false)
    private Member reportedMember;

    @Size(max = 200)
    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @NotNull
    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

}