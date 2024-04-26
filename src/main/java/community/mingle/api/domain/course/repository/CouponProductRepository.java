package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.CouponProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponProductRepository extends JpaRepository<CouponProduct, Integer> {
}
