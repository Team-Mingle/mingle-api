package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseEvaluation;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEvaluationRepository extends JpaRepository<CourseEvaluation, Long>{

    public Optional<CourseEvaluation> findByMemberAndCourse(Member member, Course course);

    public List<CourseEvaluation> findAllByCourse(Course course);

    public List<CourseEvaluation> findAllByMemberId(Long memberId);
}
