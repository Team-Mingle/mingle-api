package community.mingle.api.domain.auth.controller.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {

    @Email(message = "잘못된 이메일 형식입니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "인증코드를 입력해주세요")
    private String verificationCode;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message =  "비밀번호가 너무 짧습니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,20}$", message = "비밀번호는 6자 이상 20자 이하 영문,숫자를 포함해야 합니다.")
    private String updatePassword;
}
