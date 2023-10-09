package community.mingle.api.src.auth;

import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.src.auth.model.PostEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthFacadeService {
    private final MemberService memberService;
    private final EmailService emailService;


    public String verifyStatusEmail(PostEmailRequest postEmailRequest) {

        String email = postEmailRequest.getEmail();
        String domain = email.split("@")[1];
        if (domain.equals("freshman.mingle.com")) {
            return "새내기용 이메일입니다.";
        }

        String authKey = emailService.createCode();

        //메일 보내기
        emailService.sendAuthEmail(email,authKey);

        //메일 보내고 DB에 코드 저장
        memberService.registerAuthEmail(email, authKey);
        return "인증번호가 전송되었습니다.";
    }
}
