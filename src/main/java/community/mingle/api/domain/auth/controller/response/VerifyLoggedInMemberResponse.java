package community.mingle.api.domain.auth.controller.response;

public record VerifyLoggedInMemberResponse(
        Long memberId,
        String hashedEmail,
        String nickName,
        String univName,
        String country,
        Boolean isCourseEvaluationAllowed
) {
}
