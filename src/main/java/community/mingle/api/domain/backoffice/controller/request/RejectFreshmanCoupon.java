package community.mingle.api.domain.backoffice.controller.request;

import jakarta.validation.constraints.NotBlank;

public record RejectFreshmanCoupon(
    @NotBlank(message = "반려 사유를 입력해주세요")
    String rejectReason
) {
}
