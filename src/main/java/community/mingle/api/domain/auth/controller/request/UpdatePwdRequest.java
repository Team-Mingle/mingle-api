package community.mingle.api.domain.auth.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePwdRequest {

    @Email(message = "잘못된 이메일 형식입니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Min(value = 6, message =  "비밀번호가 너무 짧습니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,20}$", message = "비밀번호는 영문,숫자를 포함해야 합니다.")
    private String pwd;
}
