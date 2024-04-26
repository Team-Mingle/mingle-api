package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.repository.CouponProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponProductService {

    private final CouponProductRepository couponProductRepository;

    public List<CouponProduct> getAll() {
        return couponProductRepository.findAll();
    }

}
