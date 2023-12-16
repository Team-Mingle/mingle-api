package community.mingle.api.domain.auth.controller.request;

import lombok.Getter;

@Getter
public class ReissueTokenRequest {
    String encryptedEmail;
}
