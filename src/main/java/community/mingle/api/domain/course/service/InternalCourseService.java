package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.controller.request.AddCrawledCourseRequest;
import community.mingle.api.domain.course.entity.Course;
import community.mingle.api.domain.course.entity.CourseTime;
import community.mingle.api.domain.course.repository.CourseRepository;
import community.mingle.api.domain.course.repository.CourseTimeRepository;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.domain.member.repository.UniversityRepository;
import community.mingle.api.enums.Semester;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalCourseService {

    private final CourseRepository courseRepository;
    private final CourseTimeRepository courseTimeRepository;
    private final UniversityRepository universityRepository;

    @Transactional
    public void wow(AddCrawledCourseRequest request) {
        University university = universityRepository.findById(7).get();
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .name(request.getName())
                .semester(Semester.findSemester(2024, request.getSemester()))
                .venue(request.getVenue())
                .professor(request.getProfessor())
                .subclass(request.getSubclass())
                .memo(request.getMemo())
                .prerequisite(request.getPrerequisite())
                .university(university)
                .build();
        Course savedCourse = courseRepository.save(course);

        request.getCourseTimeList().stream().forEach(requestCourseTime -> {
            CourseTime courseTime = CourseTime.builder()
                    .dayOfWeek(requestCourseTime.getDayOfWeek())
                    .startTime(requestCourseTime.getStartTime())
                    .endTime(requestCourseTime.getEndTime())
                    .course(savedCourse)
                    .build();
            courseTimeRepository.save(courseTime);
        });

        log.info("successfully saved {}", savedCourse);
    }
}
