package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CrawledCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawledCourseRepository extends JpaRepository<CrawledCourse, Long> {

    @Query("SELECT c FROM CrawledCourse c WHERE c.name LIKE %:keyword% OR c.courseCode LIKE %:keyword%")
    List<CrawledCourse> findByKeyword(String keyword);

}
