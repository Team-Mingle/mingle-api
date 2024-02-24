package community.mingle.api.domain.auth.controller.response;

public record UniversityResponse(
        int universityId,
        String displayUniversityName,
        String domain
) {
}
