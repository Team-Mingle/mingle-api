package community.mingle.api.domain.course.controller.request;

import community.mingle.api.enums.CouponType;

public record CreateCouponRequest(
        CouponType couponType
) {
}
