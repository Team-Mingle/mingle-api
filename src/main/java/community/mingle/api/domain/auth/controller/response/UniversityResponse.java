package community.mingle.api.domain.auth.controller.response;

import lombok.Data;

public record UniversityResponse(
        int universityId,
        String domain
) {
}
