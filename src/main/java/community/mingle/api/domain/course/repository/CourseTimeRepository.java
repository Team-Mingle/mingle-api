package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CourseTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTimeRepository extends JpaRepository<CourseTime, Long> {
}
