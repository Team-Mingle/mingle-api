package community.mingle.api.domain.auth.controller.response;

import java.util.List;

public record UniversityResponse(
        int universityId,
        String displayUniversityName,
        List<String> domain
) {
}
