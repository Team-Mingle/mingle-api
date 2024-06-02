package community.mingle.api.domain.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.annotation.Description;

public record FreshmanSignupRequest(
    @NotBlank
    @Email
    @Description("학교 공식 도메인의 이메일")
    String email
) {
}

