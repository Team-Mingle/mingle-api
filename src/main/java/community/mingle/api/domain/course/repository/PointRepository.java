package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

}
