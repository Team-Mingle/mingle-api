package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.VerificationCodeRequest;
import community.mingle.api.domain.auth.controller.request.EmailRequest;
import community.mingle.api.domain.auth.controller.response.SendVerificationCodeResponse;
import community.mingle.api.domain.auth.controller.response.VerifyCodeResponse;
import community.mingle.api.domain.auth.controller.response.VerifyEmailResponse;
import community.mingle.api.domain.auth.service.AuthService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.request.UpdatePasswordRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.auth.service.TokenService.TokenResult;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static community.mingle.api.domain.auth.service.AuthService.FRESHMAN_EMAIL_DOMAIN;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final MemberService memberService;
    private final AuthService authService;
    private final TokenService tokenService;

    public VerifyEmailResponse verifyEmail(EmailRequest emailRequest) {
        authService.verifyEmail(emailRequest.getEmail());
        return new VerifyEmailResponse(true);
    }

    @Transactional
    public SendVerificationCodeResponse sendVerificationCodeEmail(EmailRequest emailRequest) {

        String email = emailRequest.getEmail();
        String domain = authService.extractDomain(email);
        if (!domain.equals(FRESHMAN_EMAIL_DOMAIN)) {
            String authKey = authService.createCode();
            authService.registerAuthEmail(email, authKey);
            authService.sendAuthEmail(email,authKey);
        }
        return new SendVerificationCodeResponse(true);
    }


    public VerifyCodeResponse verifyCode(VerificationCodeRequest verificationCodeRequest) {
        Boolean verified = authService.verifyCode(verificationCodeRequest.getEmail(), verificationCodeRequest.getCode());
        return new VerifyCodeResponse(verified);
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        //이메일 암호화 및 이메일, 비밀번호 확인 로직
        Member member = memberService.isValidEmailAndPassword(request.getEmail(), request.getPwd());

        //신고된 유저, 탈퇴한 유저 확인
        memberService.validateLoginMemberStatusIsActive(member);

        //토큰 생성
        Long memberId = member.getId();
        MemberRole memberRole = member.getRole();
        TokenResult tokens = tokenService.createTokens(memberId, memberRole);

        //FCM 토큰 지정
        memberService.updateFcmToken(member, request.getFcmToken());

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickName(member.getNickname())
                .univName(member.getUniversity().getName())
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }


    @Transactional
    public TokenResponse reissueAccessToken(String refreshToken, String email) {
        //JWT, refresh token DB 유효성 검사
        TokenDto tokenDto = tokenService.verifyToken(refreshToken);
        tokenService.validateRefreshToken(refreshToken);

        TokenResult tokens = tokenService.createTokens(tokenDto.getMemberId(), tokenDto.getMemberRole());
        tokenService.saveRefreshToken(email, tokens.refreshToken(), Duration.of(30, ChronoUnit.DAYS));

        return TokenResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }

    @Transactional
    public String updatePwd(UpdatePasswordRequest request) {
        Member member = memberService.isValidEmailAndPassword(request.getEmail(), request.getPwd());
        memberService.updatePwd(member, request.getPwd());
        return "비밀번호 변경에 성공하였습니다.";
    }
}
