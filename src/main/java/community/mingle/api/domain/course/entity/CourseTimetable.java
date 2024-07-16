package community.mingle.api.domain.course.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE course_timetable SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "course_timetable")
public class CourseTimetable extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Size(max = 1023)
    @Column(name = "venue", length = 1023)
    String venue;

    @Size(max = 1023)
    @Column(name = "professor", length = 1023)
    String professor;

    @Size(max = 1023)
    @Column(name = "subclass", length = 1023)
    String subclass;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timetable_id", nullable = false)
    private Timetable timetable;

    @NotNull
    @Column(name = "rgb", nullable = false)
    private String rgb;


    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateCourseTimetable(String venue, String professor, String subclass) {
        this.venue = venue;
        this.professor = professor;
        this.subclass = subclass;
    }
}