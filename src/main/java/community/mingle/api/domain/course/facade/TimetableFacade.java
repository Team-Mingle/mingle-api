package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableNameRequest;
import community.mingle.api.domain.course.controller.response.CreateTimetableResponse;
import community.mingle.api.domain.course.controller.response.UpdateTimetableCourseResponse;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.course.service.TimetableService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.CourseType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.COURSE_TIME_CONFLICT;

@Service
@RequiredArgsConstructor
public class TimetableFacade {


    private final TimetableService timetableService;
    private final CourseService courseService;
    private final TokenService tokenService;
    private final MemberService memberService;

    @Transactional
    public CreateTimetableResponse createTimetable(CreateTimetableRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.createTimetable(member, request.year(), request.semester());

        return new CreateTimetableResponse(
            timetable.getId(),
            timetable.getName(),
            timetable.getSemester()
        );
    }

    @Transactional
    public UpdateTimetableCourseResponse updateTimetableCourse(Long timetableId, Long courseId) {
        Timetable timetable = timetableService.getById(timetableId);

        Course course = courseService.getCourseById(courseId);
        List<CourseTimeDto> courseTimeDtoList = course.getCourseTimeList().stream().map(CourseTime::toDto).toList();

        if (timetableService.isCourseTimeConflictWithTimetable(timetable, courseTimeDtoList)) {
            throw new CustomException(COURSE_TIME_CONFLICT);
        }

        timetableService.addCourse(timetable, course);
        return new UpdateTimetableCourseResponse(true);
    }

    @Transactional
    public void deleteTimetableCourse(Long timetableId, Long courseId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        Timetable timetable = timetableService.getById(timetableId);
        Course course = courseService.getCourseById(courseId);
        timetableService.deleteCourse(timetable, course);

        if(course.getType() == CourseType.PERSONAL) {
            courseService.deletePersonalCourse(courseId, memberId);
        }
    }

    @Transactional
    public void deleteTimetable(Long timetableId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId);
        timetableService.deleteTimetable(timetable, member);
    }

    @Transactional
    public void updateTimetableName(Long timetableId, UpdateTimetableNameRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId);
        timetableService.updateTimetableName(timetable, member, request.name());
    }

    @Transactional
    public void convertPinStatus(Long timetableId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Timetable timetable = timetableService.getById(timetableId);
        timetableService.convertPinStatus(timetable, member);
    }
}
