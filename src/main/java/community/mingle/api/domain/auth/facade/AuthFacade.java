package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.PostCodeRequest;
import community.mingle.api.domain.auth.controller.request.PostEmailRequest;
import community.mingle.api.domain.auth.service.EmailService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthFacade {
    private final MemberService memberService;
    private final EmailService emailService;

    public String verifyEmail(PostEmailRequest postEmailRequest) {
        memberService.verifyEmail(postEmailRequest.getEmail());
        return "이메일 확인 성공.";
    }

    @Transactional
    public String verifyStatusEmail(PostEmailRequest postEmailRequest) {

        String email = postEmailRequest.getEmail();
        String domain = email.split("@")[1];
        if (domain.equals("freshman.mingle.com")) {
            return "새내기용 이메일입니다.";
        }

        String authKey = emailService.createCode();
        emailService.sendAuthEmail(email,authKey);
        memberService.registerAuthEmail(email, authKey);
        return "인증번호가 전송되었습니다.";
    }


    public String verifyCode(PostCodeRequest postCodeRequest) {
        String response = memberService.verifyCode(postCodeRequest.getEmail(), postCodeRequest.getCode());
        return response;
    }
}
