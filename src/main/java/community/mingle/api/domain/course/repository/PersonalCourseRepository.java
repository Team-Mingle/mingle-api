package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.PersonalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalCourseRepository extends JpaRepository<PersonalCourse, Long> {
}
