package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CourseTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTimetableRepository extends JpaRepository<CourseTimetable, Long> {
}
