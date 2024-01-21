package community.mingle.api.domain.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public record WithdrawMemberRequest(
        @Email(message = "잘못된 이메일 형식입니다.") @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String pwd
) {
}
