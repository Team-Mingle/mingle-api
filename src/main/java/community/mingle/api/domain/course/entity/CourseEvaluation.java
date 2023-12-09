package community.mingle.api.domain.course.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.Semester;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE course_evaluation SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "course_evaluation")
public class CourseEvaluation extends AuditLoggingBase {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Size(max = 1023)
    @NotNull
    @Column(name = "semester", nullable = false, length = 1023)
    @Enumerated(EnumType.STRING)
    private Semester semester;

    @NotNull
    @Column(name = "rating", nullable = false)
    private String rating;

    @Size(max = 1023)
    @NotNull
    @Column(name = "comment", nullable = false, length = 1023)
    private String comment;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}