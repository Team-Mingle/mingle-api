package community.mingle.api.domain.backoffice.controller.response;

import java.util.List;

public record TempSignUpApplyListResponse(
    List<TempSignUpApplyResponse> tempSignUpApplyList
) {
}
