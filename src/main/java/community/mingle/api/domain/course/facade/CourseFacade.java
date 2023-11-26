package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.controller.response.CourseDetailResponse;
import community.mingle.api.domain.course.controller.response.CoursePreviewResponse;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CrawledCourse;
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

import static community.mingle.api.global.exception.ErrorCode.COURSE_NOT_FOUND;
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
    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        if(!course.getUniversity().equals(member.getUniversity())){
            throw new CustomException(COURSE_NOT_FOUND);
        }

        List<CourseTimeDto> courseTimeDtoList = course.getCourseTimeList().stream()
                .map(this::toDto)
                .toList();

        return new CourseDetailResponse(
                course.getName(),
                course.getCourseCode(),
                course.getSemester(),
                courseTimeDtoList,
                course.getVenue(),
                course.getProfessor(),
                course.getSubclass(),
                course.getMemo(),
                course.getPrerequisite()
        );
    }

    public List<CoursePreviewResponse> searchCourse(String keyword) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        List<CrawledCourse> crawledCourseList = courseService.getCrawledCourseByKeyword(keyword, member.getUniversity());

        return crawledCourseList.stream()
                .map(course -> new CoursePreviewResponse(
                        course.getId(),
                        course.getName(),
                        course.getCourseCode(),
                        course.getSemester(),
                        course.getProfessor(),
                        course.getSubclass()
                ))
                .toList();
    }

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

    private CourseTimeDto toDto(CourseTime courseTime) {
        return new CourseTimeDto(
                courseTime.getDayOfWeek(),
                courseTime.getStartTime(),
                courseTime.getEndTime()
        );
    }
}