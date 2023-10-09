package community.mingle.api.domain.member.controller;

import community.mingle.api.domain.member.controller.request.PostCodeRequest;
import community.mingle.api.domain.member.controller.request.PostEmailRequest;
import community.mingle.api.domain.member.facade.AuthFacade;
import community.mingle.api.domain.member.service.MemberService;
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

    private final AuthFacade authFacade;

    /**
     * 1.3 이메일 입력 & 중복검사 API
     */
    @PostMapping("verifyemail")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody PostEmailRequest postEmailRequest) {

        String response = authFacade.verifyEmail(postEmailRequest);
        return ResponseEntity.ok().body(response);

    }

    /**
     * 1.4 인증코드 전송 API
     */
    @PostMapping("sendcode")
    public ResponseEntity<String> sendCode(@Valid @RequestBody PostEmailRequest postEmailRequest) {

        String response = authFacade.verifyStatusEmail(postEmailRequest);
        return ResponseEntity.ok().body(response);

    }

    /**
     * 1.5 인증 코드 검사 API
     */
    @PostMapping("verifycode")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody PostCodeRequest postCodeRequest) {

        String response = authFacade.verifyCode(postCodeRequest);
        return ResponseEntity.ok().body(response);
    }
}
