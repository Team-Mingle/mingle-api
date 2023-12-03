package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CourseTimetable;
import community.mingle.api.domain.course.entity.Timetable;
import community.mingle.api.domain.course.repository.CourseTimetableRepository;
import community.mingle.api.domain.course.repository.TimetableRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.enums.Semester;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static community.mingle.api.global.exception.ErrorCode.TIMETABLE_NOT_FOUND;

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

    public boolean isCourseTimeValid(Timetable timetable, Course newCourse) {
        List<CourseTimetable> existingCourses = timetable.getCourseTimetableList();


        return existingCourses.stream()
                .flatMap(existingCourse -> existingCourse.getCourse().getCourseTimeList().stream())
                .noneMatch(existingCourseTime -> isTimeOverlap(existingCourseTime, newCourse.getCourseTimeList()));
    }

    private boolean isTimeOverlap(CourseTime existingTime, List<CourseTime> newTimes) {
        return newTimes.stream()
                .anyMatch(newTime ->
                        existingTime.getDayOfWeek() == newTime.getDayOfWeek() &&
                                !(newTime.getEndTime().isBefore(existingTime.getStartTime()) ||
                                        newTime.getStartTime().isAfter(existingTime.getEndTime()))
                );
    }
}
