package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.PersonalCourse;
import community.mingle.api.domain.course.repository.PersonalCourseRepository;
import community.mingle.api.domain.member.entity.University;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final PersonalCourseRepository personalCourseRepository;

    @Transactional
    public PersonalCourse createPersonalCourse(
            String courseCode,
            String name,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String venue,
            String professor,
            String memo,
            University university
    ) {
        PersonalCourse course = PersonalCourse.builder()
                .courseCode(courseCode)
                .name(name)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .venue(venue)
                .professor(professor)
                .memo(memo)
                .university(university)
                .build();

        return personalCourseRepository.save(course);
    }
}
