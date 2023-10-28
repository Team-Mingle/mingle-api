package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.VerificationCodeRequest;
import community.mingle.api.domain.auth.controller.request.EmailRequest;
import community.mingle.api.domain.auth.controller.response.VerifyEmailResponse;
import community.mingle.api.domain.auth.service.EmailService;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthFacade {
    private final MemberService memberService;
    private final EmailService emailService;
    private final TokenService tokenService;

    public static final String FRESHMAN_EMAIL_DOMAIN = "freshman.mingle.com";

    public VerifyEmailResponse verifyEmail(EmailRequest emailRequest) {
        memberService.verifyEmail(emailRequest.getEmail());
        return new VerifyEmailResponse(true);
    }

    @Transactional
    public VerifyEmailResponse sendVerificationCodeEmail(EmailRequest emailRequest) {

        String email = emailRequest.getEmail();
        String domain = "@".split(email)[1];
        if (domain.equals(FRESHMAN_EMAIL_DOMAIN)) {
            String authKey = emailService.createCode();
            emailService.sendAuthEmail(email,authKey);
            memberService.registerAuthEmail(email, authKey);
        }
        return new VerifyEmailResponse(true);
    }


    public String verifyCode(VerificationCodeRequest verificationCodeRequest) {
        String response = memberService.verifyCode(verificationCodeRequest.getEmail(), verificationCodeRequest.getCode());
        return response;
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        //이메일 암호화 및 이메일, 비밀번호 확인 로직
        Member member = memberService.isValidEmailAndPwd(request.getEmail(), request.getPwd());

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
        Member member = memberService.isValidEmailAndPwd(request.getEmail(), request.getPwd());
        memberService.updatePwd(member, request.getPwd());
        return "비밀번호 변경에 성공하였습니다.";
    }
}
