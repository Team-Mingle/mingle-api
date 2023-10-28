package community.mingle.api.domain.auth.controller;

import community.mingle.api.domain.auth.controller.request.*;
import community.mingle.api.domain.auth.controller.response.CountryResponse;
import community.mingle.api.domain.auth.controller.response.DomainResponse;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.facade.AuthFacade;
import community.mingle.api.domain.auth.facade.TokenResponse;
import community.mingle.api.domain.member.service.CountryService;
import community.mingle.api.domain.member.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Auth Controller", description = "회원가입 process 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final CountryService countryService;
    private final UniversityService universityService;


    @Operation(summary = "health check ping api")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }

    @Operation(summary = "국가 리스트 api")
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getCountries() {
        List<CountryResponse> countries = countryService.getCountries();
        return ResponseEntity.ok().body(countries);
    }

    @Operation(summary = "학교 및 도메인 리스트 불러오기 api")
    @GetMapping("/email-domains")
    public ResponseEntity<List<DomainResponse>> getEmailDomains() {
        List<DomainResponse> domains = universityService.getDomains();
        return ResponseEntity.ok().body(domains);
    }

    @Operation(summary = "이메일 입력 & 중복검사 api")
    @PostMapping("/verifyemail")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailRequest emailRequest) {

        authFacade.verifyEmail(emailRequest);
        return ResponseEntity.ok().body("이메일 확인 성공");

    }

    @Operation(summary = "이메일 인증코드 전송 api")
    @PostMapping("/sendcode")
    public ResponseEntity<String> sendCode(@Valid @RequestBody EmailRequest emailRequest) {

        String response = authFacade.verifyStatusEmail(emailRequest);
        return ResponseEntity.ok().body(response);

    }

    @Operation(summary = "이메일 인증 코드 검사 api")
    @PostMapping("/verifycode")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest) {

        String response = authFacade.verifyCode(verificationCodeRequest);
        return ResponseEntity.ok().body(response);
    }
    @Operation(summary = "로그인 api")
    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(@RequestBody @Validated LoginMemberRequest request) {
        return new ResponseEntity<>(authFacade.login(request), HttpStatus.OK);
    }


    @Operation(summary = "토큰 재발급 api")
    @PostMapping("refresh-token")
    public ResponseEntity<TokenResponse> reissueAccessToken(
            @RequestHeader(value = "Authorization") String refreshToken,
            @RequestBody ReissueTokenRequest request) {
        return new ResponseEntity<>(authFacade.reissueAccessToken(refreshToken, request.getEmail()), HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정 api")
    @PatchMapping("/pwd")
    public ResponseEntity<String> updatePwd(@RequestBody @Validated UpdatePasswordRequest request) {
        return new ResponseEntity<>(authFacade.updatePwd(request), HttpStatus.OK);
    }

}
