package community.mingle.api.domain.backoffice.controller.response;

public record TempSignUpApplyResponse(
    String photoUrl,
    String nickname,
    String studentId,
    String universityName,
    String email
) {
}
