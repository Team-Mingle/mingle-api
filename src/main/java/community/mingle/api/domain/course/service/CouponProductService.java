package community.mingle.api.domain.course.service;

import community.mingle.api.domain.course.entity.CouponProduct;
import community.mingle.api.domain.course.repository.CouponProductRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.COUPON_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CouponProductService {

    private final CouponProductRepository couponProductRepository;

    public List<CouponProduct> getAll() {
        return couponProductRepository.findAll();
    }

    public CouponProduct getById(Integer id) {
        return couponProductRepository.findById(id)
                .orElseThrow(() -> new CustomException(COUPON_TYPE_NOT_FOUND));
    }

}
