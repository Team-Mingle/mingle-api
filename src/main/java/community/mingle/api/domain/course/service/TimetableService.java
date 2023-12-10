package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CourseTimetable;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.repository.CourseTimetableRepository;
import community.mingle.api.domain.course.repository.TimetableRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final MemberRepository memberRepository;
    private final CourseTimetableRepository courseTimetableRepository;

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

    @Transactional
    public void deleteTimetable(Timetable timetable, Member member) {
        hasPermission(member, timetable);

        timetableRepository.delete(timetable);


        List<Timetable> timetableList = timetableRepository.findAllByMemberOrderByOrderNumberAsc(member);
        IntStream.range(0, timetableList.size())
                .forEach(indexNumber -> timetableList.get(indexNumber).updateOrderNumber(indexNumber + 1));

    }

    public boolean isCourseTimeConflictWithTimetable(Timetable timetable, List<CourseTimeDto> courseTimeList) {
        List<CourseTimetable> existingCourses = timetable.getCourseTimetableList();


        return existingCourses.stream()
                .flatMap(existingCourse -> existingCourse.getCourse().getCourseTimeList().stream())
                .anyMatch(existingCourseTime -> isTimeOverlap(existingCourseTime, courseTimeList));
    }

    private boolean isTimeOverlap(CourseTime existingTime, List<CourseTimeDto> newTimes) {
        return newTimes.stream()
                .anyMatch(newTime ->
                        existingTime.getDayOfWeek() == newTime.dayOfWeek() &&
                                !(newTime.endTime().isBefore(existingTime.getStartTime()) ||
                                        newTime.startTime().isAfter(existingTime.getEndTime()))
                );
    }

    private void hasPermission(Member member, Timetable timetable) {
        if (!timetable.getMember().getId().equals(member.getId())) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
    }


}
