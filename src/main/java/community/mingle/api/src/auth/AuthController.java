package community.mingle.api.src.auth;

import community.mingle.api.src.auth.model.PostCodeRequest;
import community.mingle.api.src.auth.model.PostEmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthFacadeService authFacadeService;

    /**
     * 1.3 이메일 입력 & 중복검사 API
     */
    @PostMapping("email-verification")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody PostEmailRequest postEmailRequest) {

        memberService.verifyEmail(postEmailRequest);
        return ResponseEntity.ok().body("이메일 확인 성공");

    }

    /**
     * 1.4 인증코드 전송 API
     */
    @PostMapping("code")
    public ResponseEntity<String> sendCode(@Valid @RequestBody PostEmailRequest postEmailRequest) {

        String response = authFacadeService.verifyStatusEmail(postEmailRequest);
        return ResponseEntity.ok().body(response);

    }

    /**
     * 1.5 인증 코드 검사 API
     */
    @PostMapping("code-verification")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody PostCodeRequest postCodeRequest) {

        String response = memberService.verifyCode(postCodeRequest);
        return ResponseEntity.ok().body(response);
    }
}
