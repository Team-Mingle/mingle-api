package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableNameRequest;
import community.mingle.api.domain.course.controller.response.*;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    public TimetableListResponse getTimetableList() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        List<Timetable> timetableList = timetableService.getTimetableList(member);
        Map<Semester, List<TimetablePreviewResponse>> semesterListMap = timetableList.stream()
                .collect(Collectors.groupingBy(
                        Timetable::getSemester,
                        () -> new TreeMap<>(
                                Comparator.comparing(Semester::getYear)
                                        .thenComparing(Semester::getSemester).reversed()
                        ),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                timetables -> timetableService.orderTimetableList(timetables)
                                        .stream()
                                        .map(timetable -> new TimetablePreviewResponse(
                                                timetable.getId(),
                                                timetable.getSemester(),
                                                timetable.getName(),
                                                timetable.getOrderNumber(),
                                                timetable.getIsPinned()
                                        ))
                                        .collect(Collectors.toList())
                        )
                        )
                );

        return new TimetableListResponse(semesterListMap);
    }

    public TimetableDetailResponse getTimetableDetail(Long timetableId) {
        Timetable timetable = timetableService.getById(timetableId);
        List<CoursePreviewResponse> coursePreviewResponseList = timetable.getCourseTimetableList().stream()
                .map(courseTimetable -> {
                    Course course = courseTimetable.getCourse();
                    return new CoursePreviewResponse(
                            course.getId(),
                            course.getName(),
                            course.getCourseCode(),
                            course.getSemester(),
                            course.getProfessor(),
                            course.getSubclass(),
                            course.getCourseTimeList().stream()
                                    .map(CourseTime::toDto)
                                    .toList()
                    );
                })
                .toList();

        return new TimetableDetailResponse(
                timetable.getName(),
                timetable.getSemester(),
                coursePreviewResponseList
        );
    }
}
