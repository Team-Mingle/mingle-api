package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateCourseEvaluationRequest;
import community.mingle.api.domain.course.controller.response.CourseEvaluationResponse;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseEvaluation;
import community.mingle.api.domain.course.service.CourseEvaluationService;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.course.CourseEvaluationDto;
import community.mingle.api.enums.Semester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEvaluationFacade {

    private final CourseEvaluationService courseEvaluationService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final CourseService courseService;

    public void create(CreateCourseEvaluationRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Course course = courseService.getCrawledCourseById(request.courseId());

        Semester semesterEnum = Semester.findSemester(request.year(), request.semester());

        courseEvaluationService.create(
                member,
                course,
                semesterEnum,
                request.comment(),
                request.rating()
        );
    }

    public CourseEvaluationResponse getCourseEvaluationList(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        List<CourseEvaluation> courseEvaluationList = courseEvaluationService.getByCourse(course);
        List<CourseEvaluationDto> courseEvaluationDtoList = courseEvaluationList.stream()
                .map(courseEvaluation -> {
                    return new CourseEvaluationDto(
                            courseEvaluation.getId(),
                            courseEvaluation.getSemester(),
                            courseEvaluation.getComment(),
                            courseEvaluation.getRating()
                    );
                }).toList();

        return new CourseEvaluationResponse(courseEvaluationDtoList);
    }
}
