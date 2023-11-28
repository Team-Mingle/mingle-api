package community.mingle.api.domain.course.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@SuperBuilder
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE course SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("PERSONAL")
public class PersonalCourse extends Course {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public PersonalCourse updatePersonalCourse(
        Long memberId,
        String courseCode,
        String name,
        String venue,
        String professor,
        String memo
    ) {
        if(!memberId.equals(this.member.getId())){
            throw new CustomException(ErrorCode.COURSE_NOT_FOUND);
        }

        this.courseCode = courseCode;
        this.name = name;
        this.venue = venue;
        this.professor = professor;
        this.memo = memo;
        return this;
    }
}
