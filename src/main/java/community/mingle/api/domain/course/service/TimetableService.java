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

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final CourseTimetableRepository courseTimetableRepository;
    private final CourseRepository courseRepository;

    public Timetable createTimetable(Member member, int year, int semester) {

        Semester semesterEnum = Semester.findSemester(year, semester);
        List<Timetable> timetableList = timetableRepository.findAllByMemberAndSemesterOrderByOrderNumberDesc(member, semesterEnum);

        int orderNumber;
        if(timetableList.isEmpty()) orderNumber = 1;
        else orderNumber = timetableList.get(0).getOrderNumber() + 1;

        String defaultName = "시간표 " + orderNumber;

        Timetable timetable = Timetable.builder()
                .name(defaultName)
                .semester(semesterEnum)

                .orderNumber(orderNumber)
                .isPinned(false)
                .member(member)
                .build();

        return timetableRepository.save(timetable);
    }

    public Timetable getById(Long timetableId, Member member) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new CustomException(TIMETABLE_NOT_FOUND));
        hasPermission(member, timetable);
        return timetable;
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

    @Transactional
    public void deleteTimetable(Timetable timetable, Member member) {
        hasPermission(member, timetable);
        Semester semester = timetable.getSemester();
        timetableRepository.delete(timetable);


        List<Timetable> timetableList = timetableRepository.findAllByMemberAndSemesterOrderByOrderNumberAsc(member, semester);
        IntStream.range(0, timetableList.size())
                .forEach(indexNumber -> timetableList.get(indexNumber).updateOrderNumber(indexNumber + 1));

    }

    @Transactional
    public void updateTimetableName(Timetable timetable, Member member, String name) {
        hasPermission(member, timetable);
        timetable.updateName(name);
    }

    @Transactional
    public void convertPinStatus(Timetable timetable, Member member) {
        hasPermission(member, timetable);
        if (!timetable.getIsPinned()) {
            timetableRepository.findByMemberAndSemesterAndIsPinnedIsTrue(member, timetable.getSemester())
                    .ifPresent(Timetable::convertPinStatus);
        }
        timetable.convertPinStatus();
    }

    @Transactional
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

    private void hasPermission(Member member, Timetable timetable) {
        if (!timetable.getMember().getId().equals(member.getId())) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
    }

    public List<Timetable> getTimetableList(Member member) {
        return timetableRepository.findAllByMember(member);
    }

    public List<Timetable> orderTimetableList(List<Timetable> timetableList) {
        return timetableList.stream()
                .sorted(Comparator.comparing(Timetable::getIsPinned, Comparator.reverseOrder())
                        .thenComparing(Timetable::getOrderNumber))
                .toList();
    }
}
