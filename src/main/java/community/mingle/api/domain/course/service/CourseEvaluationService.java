package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseEvaluation;
import community.mingle.api.domain.course.entity.Point;
import community.mingle.api.domain.course.repository.CourseEvaluationRepository;
import community.mingle.api.domain.course.repository.PointRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.enums.CourseEvaluationRating;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseEvaluationService {

    private final CourseEvaluationRepository courseEvaluationRepository;
    private final PointRepository pointRepository;

    private static final Long POINT_REWARD_BY_COURSE_EVALUATION = 100L;


    @Transactional
    public CourseEvaluation create(Member member, Course course, Semester semester, String comment, CourseEvaluationRating rating) {
        checkCourseEvaluated(member, course);
        CourseEvaluation courseEvaluation = CourseEvaluation.builder()
                .course(course)
                .semester(semester)
                .comment(comment)
                .rating(rating)
                .member(member)
                .build();

        earnPoint(member);

        return courseEvaluationRepository.save(courseEvaluation);
    }

    private void checkCourseEvaluated(Member member, Course course) {
        courseEvaluationRepository.findByMemberAndCourse(member, course)
                .ifPresent(courseEvaluation -> {
                    throw new CustomException(ErrorCode.COURSE_ALREADY_EVALUATED);
                });
    }

    private void earnPoint(Member member) {
        pointRepository.findById(member.getId())
                .ifPresentOrElse(
                    point -> point.addAmount(POINT_REWARD_BY_COURSE_EVALUATION),

                    () -> {
                        Point point = Point.builder()
                                .member(member)
                                .amount(POINT_REWARD_BY_COURSE_EVALUATION)
                                .build();
                        pointRepository.save(point);
                    }
                );
    }

}
