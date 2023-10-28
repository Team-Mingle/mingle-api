package community.mingle.api.domain.auth.controller.response;

import community.mingle.api.enums.MemberRole;
import lombok.Data;

@Data
public class VerifyCodeResponse {
    private final Boolean verified;
}
