package community.mingle.api.domain.course.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.request.UpdatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.controller.response.CourseDetailResponse;
import community.mingle.api.domain.course.controller.response.CoursePreviewResponse;
import community.mingle.api.domain.course.entity.*;
import community.mingle.api.domain.course.service.CourseService;
import community.mingle.api.domain.course.service.TimetableService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.course.CoursePreviewDto;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.CourseColourRgb;
import community.mingle.api.enums.CourseType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.amplitude.AmplitudeService;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CourseFacade {
    private final CourseService courseService;
    private final MemberService memberService;
    private final TimetableService timetableService;
    private final TokenService tokenService;
    private final AmplitudeService amplitudeService;

    @Transactional
    public CreatePersonalCourseResponse createPersonalCourse(Long timetableId, CreatePersonalCourseRequest request) {

        if (isCourseTimeConflict(request.courseTimeDtoList())) {
            throw new CustomException(COURSE_TIME_CONFLICT);
        }

        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Timetable timetable = timetableService.getById(timetableId, member);

        timetableService.deleteConflictCoursesByOverrideValidation(timetable, request.courseTimeDtoList(), request.overrideValidation());

        PersonalCourse personalCourse = courseService.createPersonalCourse(
                request.courseCode(),
                request.name(),
                request.courseTimeDtoList(),
                request.venue(),
                request.professor(),
                request.subclass(),
                request.memo(),
                member.getUniversity(),
                member,
                timetable.getSemester()
        );

        timetableService.addCourse(timetable, personalCourse);

        amplitudeService.log(memberId, "createPersonalCourse", Map.of("personalCourseId", personalCourse.getId().toString(), "personalCourseName", personalCourse.getName()));

        return new CreatePersonalCourseResponse(
                personalCourse.getId(),
                personalCourse.getName(),
                mapToCourseTimeDto(personalCourse.getCourseTimeList()),
                personalCourse.getCourseCode(),
                personalCourse.getVenue(),
                personalCourse.getProfessor(),
                personalCourse.getSubclass(),
                //personalCourse는 하나의 timetable에 밖에 속하지 않으므로 리스트의 첫번째 timetable을 가져와도 무방
                personalCourse.getCourseTimetableList().get(0).getRgb()
        );
    }

    @Transactional
    public CourseDetailResponse updateCourse(UpdatePersonalCourseRequest request, Long courseId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        PersonalCourse personalCourse = courseService.getPersonalCourseById(courseId);

        List<Timetable> timetables = personalCourse.getCourseTimetableList().stream()
                .map(CourseTimetable::getTimetable)
                .toList();

        timetables.forEach(timetable -> {
            timetableService.deleteConflictCoursesByOverrideValidation(timetable, request.courseTimeDtoList(), request.overrideValidation());
        });

        PersonalCourse updatedPersonalCourse = personalCourse.updatePersonalCourse(
                memberId,
                request.courseCode(),
                request.name(),
                request.venue(),
                request.professor(),
                request.memo()
        );

        boolean courseTimeChanged = isCourseTimeChanged(request.courseTimeDtoList(), personalCourse.getCourseTimeList());

        List<CourseTime> courseTimeList = updatedPersonalCourse.getCourseTimeList();

        if (courseTimeChanged) {
            if (isCourseTimeConflict(request.courseTimeDtoList())) {
                throw new CustomException(COURSE_TIME_CONFLICT);
            }
            courseTimeList = courseService.updateCourseTime(personalCourse.getId(), request.courseTimeDtoList());
        }

        List<CourseTimeDto> courseTimeDtoList = courseTimeList.stream()
                .map(CourseTime::toDto)
                .toList();

        amplitudeService.log(memberId, "updateCourse", Map.of("personalCourseId", personalCourse.getId().toString(), "personalCourseName", personalCourse.getName()));

        return new CourseDetailResponse( //TODO 참고
                updatedPersonalCourse.getId(),
                updatedPersonalCourse.getName(),
                updatedPersonalCourse.getCourseCode(),
                updatedPersonalCourse.getSemester(),
                courseTimeDtoList,
                updatedPersonalCourse.getVenue(),
                updatedPersonalCourse.getProfessor(),
                updatedPersonalCourse.getSubclass(),
                updatedPersonalCourse.getMemo(),
                updatedPersonalCourse.getPrerequisite()
        );
    }

    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        if(!course.getUniversity().equals(member.getUniversity())){
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }

        List<CourseTimeDto> courseTimeDtoList = course.getCourseTimeList().stream()
                .map(CourseTime::toDto)
                .toList();

        amplitudeService.log(memberId, "getCourseDetail", Map.of("personalCourseId", course.getId().toString(), "personalCourseName", course.getName()));

        return new CourseDetailResponse(
                course.getId(),
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

    public CoursePreviewResponse searchCourse(String keyword, int year, int semester, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Semester semesterEnum = Semester.findSemester(year, semester);
        Page<CrawledCourse> crawledCourseList = courseService.getCrawledCourseByKeywordAndSemester(keyword, member.getUniversity(), semesterEnum, pageRequest);
        int totalCount = (int) crawledCourseList.getTotalElements();

        List<CoursePreviewDto> coursePreviewDtoList = crawledCourseList.stream()
                .map(course -> {
                    List<CourseTimeDto> courseTimeDtoList = course.getCourseTimeList().stream()
                            .map(CourseTime::toDto)
                            .toList();
                    return new CoursePreviewDto(
                            0L, // 검색의 경우 courseTimetableId가 없으므로 0으로 넣어준다.
                            course.getId(),
                            course.getName(),
                            course.getCourseCode(),
                            course.getSemester(),
                            course.getProfessor(),
                            course.getSubclass(),
                            courseTimeDtoList,
                            course.getVenue(),
                            //rgb 필드는 timetable view에서만 사용되므로 course list view에서는 아무 값을 default로 넣어준다.
                            CourseColourRgb.FBE9EF.getStringRgb(),
                            course.getType()
                    );
                }).toList();

        amplitudeService.log(memberId, "searchCourse", Map.of("keyword", keyword));
        return new CoursePreviewResponse(coursePreviewDtoList, totalCount);
    }

    public CoursePreviewResponse searchCourseForCourseEvaluation(String keyword, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Page<CrawledCourse> crawledCourseList = courseService.getDistinctCrawledCourseByKeyword(keyword, member.getUniversity(), pageRequest);
        int totalCount = (int) crawledCourseList.getTotalElements();

        List<CoursePreviewDto> coursePreviewDtoList = crawledCourseList
                .stream()
                .map(course -> {
                    return new CoursePreviewDto(
                            0L, // 검색의 경우 courseTimetableId가 없으므로 0으로 넣어준다.
                            course.getId(),
                            course.getName(),
                            course.getCourseCode(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CourseType.CRAWL
                    );
                }).toList();

        amplitudeService.log(memberId, "searchCourseForCourseEvaluation", Map.of("keyword", keyword));
        return new CoursePreviewResponse(coursePreviewDtoList, totalCount);
    }

    private boolean isCourseTimeConflict(List<CourseTimeDto> courseTimeDtoList) {
        return courseTimeDtoList.stream()
                .flatMap(first -> courseTimeDtoList.stream()
                        .filter(second -> first != second &&
                                first.dayOfWeek().equals(second.dayOfWeek()) &&
                                isTimeOverlap(first.startTime(), first.endTime(), second.startTime(), second.endTime())))
                .findAny()
                .isPresent();
    }

    private boolean isTimeOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return (startTime1.isBefore(endTime2)) && (endTime1.isAfter(startTime2));
    }

    private boolean isCourseTimeChanged(List<CourseTimeDto> courseTimeDtoList, List<CourseTime> courseTimeList) {
        return courseTimeDtoList.stream().anyMatch(dto ->
                courseTimeList.stream().anyMatch(courseTime ->
                        !dto.dayOfWeek().equals(courseTime.getDayOfWeek()) ||
                        !dto.startTime().equals(courseTime.getStartTime()) ||
                        !dto.endTime().equals(courseTime.getEndTime()))
        );
    }

    private List<CourseTimeDto> mapToCourseTimeDto(List<CourseTime> courseTimeList) {
        return courseTimeList.stream().map(courseTime ->
            new CourseTimeDto(
                    courseTime.getDayOfWeek(),
                    courseTime.getStartTime(),
                    courseTime.getEndTime()
            )
        ).toList();
    }
}
