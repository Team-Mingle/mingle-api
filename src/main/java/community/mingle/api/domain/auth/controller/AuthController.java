package community.mingle.api.domain.auth.controller;

import community.mingle.api.domain.auth.controller.request.*;
import community.mingle.api.domain.auth.controller.response.*;
import community.mingle.api.domain.auth.facade.AuthFacade;
import community.mingle.api.domain.auth.facade.TokenResponse;
import community.mingle.api.domain.member.service.CountryService;
import community.mingle.api.domain.member.service.UniversityService;
import community.mingle.api.enums.PolicyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Auth Controller", description = "회원가입 process 관련 API")
@ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                "- EMPTY_FIELD_ERROR: 필드 빈 값 입력 오류 \n" +
                "- REGEX_ERROR: 형식 (이메일, 비밀번호, 닉네임 등) 오류 \n" +
                "- SIZE_LIMIT_ERROR: 길이 제한 (비밀번호 등) 오류", content = @Content(schema = @Schema(hidden = true))
        )}
)
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

    @Operation(summary = "1.1 국가 리스트 api")
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getCountries() {
        List<CountryResponse> countries = countryService.getCountries();
        return ResponseEntity.ok().body(countries);
    }

    @Operation(summary = "1.2 학교 및 도메인 리스트 불러오기 api")
    @GetMapping("/email-domains/{countryName}")
    public ResponseEntity<List<DomainResponse>> getEmailDomains(@PathVariable String countryName) {
        List<DomainResponse> domainResponses = universityService.getDomains(countryName);
        return ResponseEntity.ok().body(domainResponses);
    }

    @Operation(summary = "1.3 이메일 중복 검사 api")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "[CLIENT] EMAIL_DUPLICATED - 이미 존재하는 이메일 주소입니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/verifyemail")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody EmailRequest emailRequest) {

        VerifyEmailResponse verifyEmailResponse = authFacade.verifyEmail(emailRequest);
        return ResponseEntity.ok().body(verifyEmailResponse);

    }

    @Operation(summary = "1.4 이메일 인증코드 전송 api")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "[CLIENT] EMAIL_DUPLICATED - 이미 존재하는 이메일 주소입니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/sendcode")
    public ResponseEntity<SendVerificationCodeResponse> sendCode(@Valid @RequestBody EmailRequest emailRequest) {

        SendVerificationCodeResponse sendVerificationCodeResponse = authFacade.sendVerificationCodeEmail(emailRequest.getEmail());
        return ResponseEntity.ok().body(sendVerificationCodeResponse);

    }

    @Operation(summary = "1.5 이메일 인증 코드 검사 api")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "[CLIENT] CODE_MATCH_FAILED - 인증번호가 일치하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "[CLIENT] CODE_VALIDITY_EXPIRED 인증번호가 만료되었습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "CODE_FOUND_FAILED - 존재하지 않는 인증번호입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/verifycode")
    public ResponseEntity<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest) {

        VerifyCodeResponse verifyCodeResponse = authFacade.verifyCode(verificationCodeRequest);
        return ResponseEntity.ok().body(verifyCodeResponse);
    }

    @Operation(summary = "1.6 개인정보 처리방침 불러오기 api")
    @GetMapping("/privacy-policy")
    public ResponseEntity<PolicyResponse> privacyPolicy() {
        PolicyResponse policyResponse = authFacade.getPolicy(PolicyType.PRIVACY_POLICY);
        return new ResponseEntity<>(policyResponse, HttpStatus.OK);
    }

    @Operation(summary = "1.7 서비스 이용약관 불러오기 api")
    @GetMapping("/terms-and-conditions")
    public ResponseEntity<PolicyResponse> termsAndConditions() {
        PolicyResponse policyResponse = authFacade.getPolicy(PolicyType.TERMS_AND_CONDITIONS);
        return new ResponseEntity<>(policyResponse, HttpStatus.OK);
    }


    @Operation(summary = "1.8 회원가입 api")
    @ApiResponse(responseCode = "409", description =
            "- [CLIENT] CODE_MATCH_FAILED: 인증번호가 일치하지 않습니다. \n" +
                    "- [CLIENT] CODE_VALIDITY_EXPIRED: 인증번호가 만료되었습니다. \n" +
                    "- CODE_FOUND_FAILED: 존재하지 않는 인증번호입니다.",
            content = @Content(schema = @Schema(hidden = true)))
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authFacade.signUp(signUpRequest), HttpStatus.OK);
    }

    @Operation(summary = "1.9 로그인 api")
    @ApiResponse(responseCode = "404", description =
            "- [CLIENT] FAILED_TO_LOGIN: 일치하는 이메일이나 비밀번호를 찾지 못했습니다. 띄어쓰기나 잘못된 글자가 있는지 확인해 주세요. \n" +
                    "- [CLIENT] MEMBER_DELETED_ERROR: 탈퇴한 사용자입니다. \n" +
                    "- [CLIENT] MEMBER_REPORTED_ERROR: 신고된 사용자입니다. \n" +
                    "- MEMBER_NOT_FOUND: 존재하지 않는 회원 정보입니다.",
            content = @Content(schema = @Schema(hidden = true)))
    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(@Valid @RequestBody LoginMemberRequest loginMemberRequest) {
        return new ResponseEntity<>(authFacade.login(loginMemberRequest), HttpStatus.OK);
    }


    @Operation(summary = "1.10 비밀번호 재설정 api")
    @ApiResponse(responseCode = "404", description =
            "- [CLIENT] FAILED_TO_LOGIN: 일치하는 이메일이나 비밀번호를 찾지 못했습니다. 띄어쓰기나 잘못된 글자가 있는지 확인해 주세요. \n" +
                    "- MEMBER_NOT_FOUND: 존재하지 않는 회원 정보입니다.",
            content = @Content(schema = @Schema(hidden = true)))
    @PatchMapping("/password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return new ResponseEntity<>(authFacade.updatePassword(request), HttpStatus.OK);
    }


    @Operation(summary = "1.11 비밀번호 재설정용 이메일 인증코드 전송 api")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "MEMBER_NOT_FOUND - 존재하지 않는 회원 정보입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "EMAIL_SEND_FAILED - 이메일 전송에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/sendcode/pwd")
    public ResponseEntity<SendVerificationCodeResponse> sendCodeForPwdReset(@Valid @RequestBody EmailRequest emailRequest) {
        SendVerificationCodeResponse sendVerificationCodeResponse = authFacade.sendVerificationCodeEmailForPwdReset(emailRequest.getEmail());
        return ResponseEntity.ok().body(sendVerificationCodeResponse);
    }


    @Operation(summary = "1.12 토큰 재발급 api")
    @ApiResponse(responseCode = "401", description = "Unauthorized\n" +
            "- TOKEN_EXPIRED: 토큰이 만료되었습니다. \n" +
            "- TOKEN_NOT_FOUND: 일치하는 토큰을 찾지 못하였습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    @PostMapping("refresh-token")
    public ResponseEntity<TokenResponse> reissueAccessToken(
            @Parameter(in = ParameterIn.HEADER, description = "X-Refresh-Token", required = true)
            @RequestHeader(value = "X-Refresh-Token") String refreshToken,
            @RequestBody ReissueTokenRequest reissueTokenRequest
    ) {
        TokenResponse tokenResponse = authFacade.reissueAccessToken(refreshToken, reissueTokenRequest.getEmail());
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
}

