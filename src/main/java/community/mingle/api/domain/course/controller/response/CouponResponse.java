package community.mingle.api.domain.course.controller.response;

import java.time.LocalDate;

public record CouponResponse(
    String name,
    LocalDate startDate,
    LocalDate endDate

) {
}
