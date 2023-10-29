package community.mingle.api.domain.auth.controller.response;

import lombok.Data;

@Data
public class VerifyEmailResponse {
    private final Boolean verified;
}
