package community.mingle.api.domain.point.repository;

import community.mingle.api.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    public Point findByMemberId(Long memberId);
}
