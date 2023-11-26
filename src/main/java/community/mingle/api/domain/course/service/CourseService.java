package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.entity.CrawledCourse;
import community.mingle.api.domain.course.entity.PersonalCourse;
import community.mingle.api.domain.course.repository.CourseRepository;
import community.mingle.api.domain.course.repository.CourseTimeRepository;
import community.mingle.api.domain.course.repository.CrawledCourseRepository;
import community.mingle.api.domain.course.repository.PersonalCourseRepository;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.COURSE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final PersonalCourseRepository personalCourseRepository;
    private final CrawledCourseRepository crawledCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseTimeRepository courseTimeRepository;

    @Transactional
    public PersonalCourse createPersonalCourse(
            String courseCode,
            String name,
            List<CourseTimeDto> courseTimeDtoList,
            String venue,
            String professor,
            String memo,
            University university
    ) {
        PersonalCourse course = PersonalCourse.builder()
                .courseCode(courseCode)
                .name(name)
                .venue(venue)
                .professor(professor)
                .memo(memo)
                .university(university)
                .build();

        PersonalCourse personalCourse = personalCourseRepository.save(course);

        courseTimeDtoList.forEach(courseTimeDto -> {
            CourseTime courseTime = CourseTime.builder()
                    .dayOfWeek(courseTimeDto.dayOfWeek())
                    .startTime(courseTimeDto.startTime())
                    .endTime(courseTimeDto.endTime())
                    .course(personalCourse)
                    .build();
            courseTimeRepository.save(courseTime);
        });

        return personalCourse;
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
    }

    public List<CrawledCourse> getCrawledCourseByKeyword(String keyword) {
        return crawledCourseRepository.findByKeyword(keyword);
    }
}
