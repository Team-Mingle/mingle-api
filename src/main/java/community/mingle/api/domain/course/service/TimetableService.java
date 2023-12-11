package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CourseTimetable;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.repository.CourseRepository;
import community.mingle.api.domain.course.repository.CourseTimetableRepository;
import community.mingle.api.domain.course.repository.TimetableRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.CourseType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final MemberRepository memberRepository;
    private final CourseTimetableRepository courseTimetableRepository;
    private final CourseRepository courseRepository;

    public Timetable createTimetable(Long memberId, int year, int semester) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Semester semesterEnum = Semester.findSemester(year, semester);
        List<Timetable> timetableList = timetableRepository.findAllByMemberOrderByOrderNumberDesc(member);
        int orderNumber;
        if(timetableList.isEmpty()) orderNumber = 1;
        else orderNumber = timetableList.get(0).getOrderNumber() + 1;

        Timetable timetable = Timetable.builder()
                .semester(semesterEnum)
                .orderNumber(orderNumber)
                .isPinned(false)
                .member(member)
                .build();

        return timetableRepository.save(timetable);
    }

    public Timetable getById(Long timetableId) {
        return timetableRepository.findById(timetableId)
                .orElseThrow(() -> new CustomException(TIMETABLE_NOT_FOUND));
    }

    @Transactional
    public CourseTimetable addCourse(Timetable timetable, Course course) {
        CourseTimetable courseTimetable = CourseTimetable.builder()
                .timetable(timetable)
                .course(course)
                .build();
        return courseTimetableRepository.save(courseTimetable);
    }

    @Transactional
    public void deleteCourse(Timetable timetable, Course course) {
        CourseTimetable courseTimetable = courseTimetableRepository.findByTimetableIdAndCourseId(timetable.getId(), course.getId())
                .orElseThrow(() -> new CustomException(TIMETABLE_NOT_FOUND));
        courseTimetableRepository.delete(courseTimetable);
    }

    public void deleteConflictCoursesByOverrideValidation(Timetable timetable, List<CourseTimeDto> courseTimeDtoList, boolean overrideValidation) {
        List<Course> conflictCourseList = coursesConflictWithNewCourseTime(timetable, courseTimeDtoList);
        if (!overrideValidation && !conflictCourseList.isEmpty()) {
            throw new CustomException(TIMETABLE_CONFLICT);
        } else if (overrideValidation && !conflictCourseList.isEmpty()) {
            conflictCourseList.stream()
                    .filter(course -> course.getType().equals(CourseType.CRAWL))
                    .forEach(course -> courseTimetableRepository.deleteAll(course.getCourseTimetableList()));

            conflictCourseList.stream()
                    .filter(course -> course.getType().equals(CourseType.PERSONAL))
                    .forEach(courseRepository::delete);
        }
    }

    public List<Course> coursesConflictWithNewCourseTime(Timetable timetable, List<CourseTimeDto> courseTimeList) {
        List<CourseTimetable> existingCourses = timetable.getCourseTimetableList();

        return existingCourses.stream()
                .flatMap(existingCourse -> existingCourse.getCourse().getCourseTimeList().stream())
                .filter(existingCourseTime -> isTimeOverlap(existingCourseTime, courseTimeList))
                .map(CourseTime::getCourse)
                .toList();

    }

    private boolean isTimeOverlap(CourseTime existingTime, List<CourseTimeDto> newTimes) {
        return newTimes.stream()
                .anyMatch(newTime ->
                        existingTime.getDayOfWeek() == newTime.dayOfWeek() &&
                                !((newTime.endTime().isBefore(existingTime.getStartTime()) || newTime.endTime().equals(existingTime.getStartTime()))
                                        ||
                                        (newTime.startTime().isAfter(existingTime.getEndTime()) || newTime.startTime().equals(existingTime.getEndTime())))
                );
    }
}
