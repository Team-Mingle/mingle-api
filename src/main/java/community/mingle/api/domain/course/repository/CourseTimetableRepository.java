package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CourseTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseTimetableRepository extends JpaRepository<CourseTimetable, Long> {

    public Optional<CourseTimetable> findByTimetableIdAndCourseId(Long timetableId, Long courseId);
}
