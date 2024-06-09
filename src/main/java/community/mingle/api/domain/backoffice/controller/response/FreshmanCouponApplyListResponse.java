package community.mingle.api.domain.backoffice.controller.response;

import java.util.List;

public record FreshmanCouponApplyListResponse(
    List<FreshmanCouponApplyResponse> freshmanCouponApplyList
) {

    public static record FreshmanCouponApplyResponse(
        Long memberId,
        String photoUrl
    ) {
    }

}
