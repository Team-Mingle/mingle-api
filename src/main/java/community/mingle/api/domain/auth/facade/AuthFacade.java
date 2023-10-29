package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.*;
import community.mingle.api.domain.auth.controller.response.*;
import community.mingle.api.domain.auth.service.AuthService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.dto.security.CreatedTokenDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.domain.auth.service.AuthService.FRESHMAN_EMAIL_DOMAIN;
import static community.mingle.api.global.exception.ErrorCode.MEMBER_ALREADY_EXIST;
import static community.mingle.api.global.exception.ErrorCode.NICKNAME_DUPLICATED;

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
    public SignUpResponse signUp(SignUpRequest request) {
        if (memberService.existsByEmail(request.getEmail())) {
            throw new CustomException(MEMBER_ALREADY_EXIST);
        }

        if (memberService.existsByNickname(request.getNickname())) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }

        Member member = memberService.create(request.getUnivId(), request.getNickname(), request.getEmail(), request.getPassword());
        return new SignUpResponse(member.getId());



    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest loginMemberRequest) {

        Member member = memberService.getByEmail(loginMemberRequest.getEmail());

        authService.checkPassword(loginMemberRequest.getPassword(), member.getPassword());
        authService.checkMemberStatusActive(member);

        Long memberId = member.getId();
        MemberRole memberRole = member.getRole();
        String email = member.getEmail();
        CreatedTokenDto tokens = tokenService.createTokens(memberId, memberRole, email);

        memberService.setFcmToken(member, loginMemberRequest.getFcmToken());

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .hashedEmail(member.getEmail())
                .nickName(member.getNickname())
                .univName(member.getUniversity().getName())
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    @Transactional
    public TokenResponse reissueAccessToken(String refreshToken, String email) {

        TokenDto tokenDto = tokenService.verifyToken(refreshToken);
        tokenService.validateRefreshToken(refreshToken);

        CreatedTokenDto tokens = tokenService.createTokens(tokenDto.getMemberId(), tokenDto.getMemberRole(), email);

        return TokenResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    @Transactional
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        Member member = memberService.getByHashedEmail(updatePasswordRequest.getEmail());
        authService.checkPassword(updatePasswordRequest.getPassword(), member.getPassword());
        memberService.updatePassword(member, updatePasswordRequest.getPassword());
        return new UpdatePasswordResponse(true);
    }
}
