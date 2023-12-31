package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CourseTimetable;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.repository.CourseRepository;
import community.mingle.api.domain.course.repository.CourseTimetableRepository;
import community.mingle.api.domain.course.repository.TimetableRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.CourseColourRgb;
import community.mingle.api.enums.CourseType;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        boolean isPinned = false;
        if(timetableList.isEmpty()) {
            orderNumber = 1;
            isPinned = true;
        }
        else orderNumber = timetableList.get(0).getOrderNumber() + 1;

        String defaultName = "시간표 " + orderNumber;

        Timetable timetable = Timetable.builder()
                .name(defaultName)
                .semester(semesterEnum)
                .orderNumber(orderNumber)
                .isPinned(isPinned)
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

        CourseColourRgb rgb = getRandomRgb(timetable);
        CourseTimetable courseTimetable = CourseTimetable.builder()
                .timetable(timetable)
                .course(course)
                .rgb(rgb.getStringRgb())
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
    public void changePinnedTimetable(Timetable timetable, Member member) {
        hasPermission(member, timetable);
        if (timetable.getIsPinned()) {
            throw new CustomException(TIMETABLE_ALREADY_PINNED);
        }
        timetableRepository.findByMemberAndSemesterAndIsPinnedIsTrue(member, timetable.getSemester())
                .ifPresent(Timetable::convertPinStatus);
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
                    .forEach(course -> {
                        CourseTimetable courseTimetable = courseTimetableRepository
                                .findByTimetableIdAndCourseId(
                                        timetable.getId(),
                                        course.getId())
                                .get();
                        courseTimetableRepository.delete(courseTimetable);
                    });

            conflictCourseList.stream()
                    .filter(course -> course.getType().equals(CourseType.PERSONAL))
                    .forEach(courseRepository::delete);
        }
    }

    public void checkCourseAlreadyAdded(Timetable timetable, Course course) {
        courseTimetableRepository.findByTimetableIdAndCourseId(timetable.getId(), course.getId())
                .ifPresent(it -> {
                    throw new CustomException(COURSE_ALREADY_ADDED);
                });
    }

    public List<Timetable> listByIdAndIsPinnedTrue(Member member) {
        return timetableRepository.findAllByMember(member);
    }

    private List<Course> coursesConflictWithNewCourseTime(Timetable timetable, List<CourseTimeDto> courseTimeList) {
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

    private CourseColourRgb getRandomRgb(Timetable timetable) {
        List<CourseColourRgb> colourRgbs = timetable.getCourseTimetableList().stream()
                .map(it -> CourseColourRgb.getByRgb(it.getRgb()))
                .toList();

        if (colourRgbs.isEmpty()) {
            return Arrays.stream(CourseColourRgb.values())
                    .findFirst().get();
        } else if (colourRgbs.size() < CourseColourRgb.values().length) {
            return EnumSet.complementOf(EnumSet.copyOf(colourRgbs))
                    .stream()
                    .findAny().get();
        } else {
            Map<CourseColourRgb, Long> countByEnum = colourRgbs.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            long minCount = countByEnum.values().stream().min(Long::compareTo).orElse(0L);

            return countByEnum.entrySet().stream()
                    .filter(entry -> entry.getValue() == minCount)
                    .map(Map.Entry::getKey)
                    .findAny().get();
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
