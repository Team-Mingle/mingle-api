package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.PostCodeRequest;
import community.mingle.api.domain.auth.controller.request.PostEmailRequest;
import community.mingle.api.domain.auth.service.EmailService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.request.UpdatePwdRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.auth.service.TokenService.TokenResult;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.utils.EmailHasher;
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

    /**
     * 로그인
     */
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


    /**
     * 1.12 Access Token 재발급
     */
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


    /**
     * 1.10 비밀번호 초기화
     */
    @Transactional
    public String updatePwd(UpdatePwdRequest request) {
        Member member = memberService.isValidEmailAndPwd(request.getEmail(), request.getPwd());
        memberService.updatePwd(member, request.getPwd());
        return "비밀번호 변경에 성공하였습니다.";
    }
}
