package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.controller.response.GetCourseDetailResponse;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.COURSE_TIME_CONFLICT;

@Service
@RequiredArgsConstructor
public class CourseFacade {
    private final CourseService courseService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @Transactional
    public CreatePersonalCourseResponse createPersonalCourse(CreatePersonalCourseRequest request) {

        if (checkCourseTimeConflict(request.courseTimeDtoList())) {
            throw new CustomException(COURSE_TIME_CONFLICT);
        }

        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        courseService.createPersonalCourse(
                request.courseCode(),
                request.name(),
                request.courseTimeDtoList(),
                request.venue(),
                request.professor(),
                request.memo(),
                member.getUniversity()
        );

        return new CreatePersonalCourseResponse(
                request.name(),
                request.courseTimeDtoList(),
                request.courseCode(),
                request.venue()
        );
    }
//    public GetCourseDetailResponse getCourseDetail(Long courseId) {
//        Course course = courseService.getCourseById(courseId);
//        return new GetCourseDetailResponse(
//                course.getName(),
//                course.getCourseCode(),
//                course.getSemester(),
//                course.getCourseTimeDtoList(),
//                course.getVenue(),
//                course.getProfessor(),
//                course.getSubclass(),
//                course.getMemo(),
//                course.getPrerequisite()
//        );
//    }

    private boolean checkCourseTimeConflict(List<CourseTimeDto> courseTimeDtoList) {
        return courseTimeDtoList.stream()
                .flatMap(first -> courseTimeDtoList.stream()
                        .filter(second -> first != second && first.dayOfWeek().equals(second.dayOfWeek()))
                        .filter(second -> isTimeOverlap(first.startTime(), first.endTime(), second.startTime(), second.endTime())))
                .findAny()
                .isPresent();
    }

    private boolean isTimeOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return (startTime1.isBefore(endTime2)) && (endTime1.isAfter(startTime2));
    }

}
