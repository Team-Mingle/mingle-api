package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CrawledCourse;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.enums.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawledCourseRepository extends JpaRepository<CrawledCourse, Long> {

    @Query("SELECT c FROM CrawledCourse c WHERE (c.name LIKE %:keyword% OR c.courseCode LIKE %:keyword%) AND c.university = :university GROUP BY c.courseCode")
    Page<CrawledCourse> findByKeyword(@Param("keyword") String keyword, @Param("university") University university, Pageable pageable);

    @Query("SELECT c FROM CrawledCourse c WHERE c.semester = :semester AND (c.name LIKE %:keyword% OR c.courseCode LIKE %:keyword%) AND c.university = :university")
    Page<CrawledCourse> findByKeywordAndSemester(@Param("keyword") String keyword, @Param("university") University university, @Param("semester") Semester semester, Pageable pageable);

}
