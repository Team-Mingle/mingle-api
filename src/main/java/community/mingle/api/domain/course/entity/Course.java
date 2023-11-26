package community.mingle.api.domain.course.entity;

import community.mingle.api.domain.member.entity.University;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE course SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "course")
public class Course extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 1023)
    @Column(name = "course_code", length = 1023)
    private String courseCode;

    @Lob
    @Column(name = "name")
    private String name;

    @Size(max = 1023)
    @Column(name = "semester", length = 1023)
    private String semester;

    @Size(max = 1023)
    @Column(name = "venue", length = 1023)
    private String venue;

    @Size(max = 1023)
    @Column(name = "professor", length = 1023)
    private String professor;

    @Size(max = 1023)
    @Column(name = "subclass", length = 1023)
    private String subclass;

    @Lob
    @Column(name = "memo")
    private String memo;

    @Lob
    @Column(name = "prerequisite")
    private String prerequisite;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "course")
    private List<CourseTime> courseTimeList= new ArrayList<>();

}