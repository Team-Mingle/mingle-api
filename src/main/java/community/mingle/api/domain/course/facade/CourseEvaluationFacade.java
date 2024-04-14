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
import community.mingle.api.global.amplitude.AmplitudeService;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static community.mingle.api.global.exception.ErrorCode.COURSE_FORBIDDEN;

@Service
@RequiredArgsConstructor
public class CourseEvaluationFacade {

    private final CourseEvaluationService courseEvaluationService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final CourseService courseService;
    private final AmplitudeService amplitudeService;

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

        amplitudeService.log(memberId, "createCourseEvaluation", Map.of("courseId", course.getId().toString(), "semester", semesterEnum.name()));
    }

    public CourseEvaluationResponse getCourseEvaluationList(Long courseId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Course course = courseService.getCourseById(courseId);
        if (member.getUniversity().getId() != course.getUniversity().getId()) {
            throw new CustomException(COURSE_FORBIDDEN);
        }

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

        amplitudeService.log(memberId, "getCourseEvaluationList", Map.of("courseId", course.getId().toString()));
        return new CourseEvaluationResponse(courseEvaluationDtoList);
    }
}
