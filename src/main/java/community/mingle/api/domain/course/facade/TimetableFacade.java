package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.response.CreateTimetableResponse;
import community.mingle.api.domain.course.controller.response.UpdateTimetableCourseResponse;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.course.service.TimetableService;
import community.mingle.api.enums.CourseType;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static community.mingle.api.global.exception.ErrorCode.COURSE_TIME_CONFLICT;

@Service
@RequiredArgsConstructor
public class TimetableFacade {


    private final TimetableService timetableService;
    private final CourseService courseService;
    private final TokenService tokenService;

    public CreateTimetableResponse createTimetable(CreateTimetableRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Timetable timetable = timetableService.createTimetable(memberId, request.year(), request.semester());
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

        if (!timetableService.isCourseTimeValid(timetable, course)) {
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
}
