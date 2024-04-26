package community.mingle.api.domain.course.repository;

import community.mingle.api.domain.course.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>{

    public List<Coupon> findByMemberId(Long memberId);
}
