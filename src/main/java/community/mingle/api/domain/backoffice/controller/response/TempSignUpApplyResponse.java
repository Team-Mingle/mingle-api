package community.mingle.api.domain.backoffice.controller.response;

public record TempSignUpApplyResponse(
    Long memberId,
    String photoUrl,
    String nickname,
    String studentId,
    String universityName,
    String email
) {
}
