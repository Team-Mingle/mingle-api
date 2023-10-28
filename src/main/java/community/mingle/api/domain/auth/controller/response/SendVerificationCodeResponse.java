package community.mingle.api.domain.auth.controller.response;

import lombok.Data;

@Data
public class SendVerificationCodeResponse {
    private final Boolean sent;
}
