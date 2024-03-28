package community.mingle.api.domain.auth.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TempSignUpRequest(
        int univId,
        @Email(message = "잘못된 이메일 형식입니다.") @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 공백이나 이모티콘이 포함될 수 없습니다.")
        String nickname,
        @NotBlank(message = "학번 또는 지원번호를 입력해주세요.")
        String studentId,
        @NotBlank(message = "FCM 토큰을 입력해주세요.")
        String fcmToken
) {
}
