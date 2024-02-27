package community.mingle.api.domain.report.entity;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.ReportType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Where(clause = "deleted_at IS NULL")
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

//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @NotNull
    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;


    public static PostReport createPostReport(Member reporterMember, ReportType reportType, Post post) {
        return PostReport.builder()
                .reporterMember(reporterMember)
                .reportedMember(post.getMember())
                .contentType(ContentType.POST)
                .reportType(reportType)
                .post(post)
                .build();
    }

    public static CommentReport createCommentReport(Member reporterMember, ReportType reportType, Comment comment) {
        return CommentReport.builder()
                .reporterMember(reporterMember)
                .reportedMember(comment.getMember())
                .contentType(ContentType.COMMENT)
                .reportType(reportType)
                .comment(comment)
                .build();
    }
}