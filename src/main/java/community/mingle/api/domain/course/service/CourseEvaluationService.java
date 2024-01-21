package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseEvaluation;
import community.mingle.api.domain.course.repository.CourseEvaluationRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.point.event.EarningPointEvent;
import community.mingle.api.enums.CourseEvaluationRating;
import community.mingle.api.enums.PointEarningType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEvaluationService {

    private final CourseEvaluationRepository courseEvaluationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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

        applicationEventPublisher.publishEvent(
                new EarningPointEvent(this, PointEarningType.COURSE_EVALUATION, member.getId())
        );

        return courseEvaluationRepository.save(courseEvaluation);
    }

    public List<CourseEvaluation> getByCourse(Course course) {
        return courseEvaluationRepository.findAllByCourse(course);
    }

    private void checkCourseEvaluated(Member member, Course course) {
        courseEvaluationRepository.findByMemberAndCourse(member, course)
                .ifPresent(courseEvaluation -> {
                    throw new CustomException(ErrorCode.COURSE_ALREADY_EVALUATED);
                });
    }
}
