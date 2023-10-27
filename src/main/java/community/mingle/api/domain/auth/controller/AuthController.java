package community.mingle.api.domain.auth.controller;

import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.request.PostEmailRequest;
import community.mingle.api.domain.auth.controller.request.ReissueTokenRequest;
import community.mingle.api.domain.auth.controller.request.UpdatePwdRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.facade.AuthFacade;
import community.mingle.api.domain.auth.facade.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
//@Tag(name = "auth", description = "회원가입 process 관련 API") //TODO swagger 설정후 추가
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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
    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(@RequestBody @Validated LoginMemberRequest request) {
        return new ResponseEntity<>(authFacade.login(request), HttpStatus.OK);
    }


    /**
     * 1.12 토큰 재발급 API
     */
    @PostMapping("refresh-token")
    public ResponseEntity<TokenResponse> reissueAccessToken(
            @RequestHeader(value = "Authorization") String refreshToken,
            @RequestBody ReissueTokenRequest request) {
        return new ResponseEntity<>(authFacade.reissueAccessToken(refreshToken, request.getEmail()), HttpStatus.OK);
    }

    /**
     * 1.10 비밀번호 초기화 API
     */
    @PatchMapping("/pwd")
    public ResponseEntity<String> updatePwd(@RequestBody @Validated UpdatePwdRequest request) {
        return new ResponseEntity<>(authFacade.updatePwd(request), HttpStatus.OK);
    }

}
