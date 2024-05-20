package community.mingle.api.domain.course.controller.response;

public record CouponProductResponse(
    int id,
    String name,
    String description,
    Long cost
) {
}
