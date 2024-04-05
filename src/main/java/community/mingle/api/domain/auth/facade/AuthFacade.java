package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.*;
import community.mingle.api.domain.auth.controller.response.*;
import community.mingle.api.domain.auth.entity.Policy;
import community.mingle.api.domain.auth.service.AuthService;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberAuthPhotoService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.security.CreatedTokenDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.enums.PolicyType;
import community.mingle.api.enums.TempSignUpStatusType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static community.mingle.api.domain.auth.service.AuthService.FRESHMAN_EMAIL_DOMAIN;
import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final MemberService memberService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final S3Service s3Service;
    private final MemberAuthPhotoService memberAuthPhotoService;

    public VerifyEmailResponse verifyEmail(EmailRequest emailRequest) {
        authService.verifyEmail(emailRequest.getEmail());
        return new VerifyEmailResponse(true);
    }

    @Transactional
    public SendVerificationCodeResponse sendVerificationCodeEmail(String email) {
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
        if (memberService.existsByEmail(request.email())) {
            throw new CustomException(MEMBER_ALREADY_EXIST);
        }

        if (memberService.existsByNickname(request.nickname())) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }

        Member member = memberService.create(request.univId(), request.nickname(), request.email(), request.password());
        return new SignUpResponse(member.getId());
    }

    @Transactional
    public SignUpResponse tempSignUp(TempSignUpRequest request) {
        if (memberService.existsByEmail(request.email()) || memberService.existsByStudentId(request.studentId(), request.univId())) {
            throw new CustomException(MEMBER_ALREADY_EXIST);
        }
        if (memberService.existsByNickname(request.nickname())) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }

        Member member = memberService.tempCreate(request.univId(), request.nickname(), request.email(), request.password(), request.fcmToken(), request.studentId());

        List<String> imgUrls = s3Service.uploadFile(request.multipartFile(), "temp_auth");
        imgUrls.forEach(imgUrl ->
                        memberAuthPhotoService.create(member.getId(), imgUrl)
                );
        authService.sendTempSignUpEmail(request.email(), TempSignUpStatusType.PROCESSING);

        return new SignUpResponse(member.getId());
    }

    @Transactional
    public LoginMemberResponse login(LoginMemberRequest loginMemberRequest) {

        Member member;
        try {
            member = memberService.getByEmail(loginMemberRequest.getEmail());
        } catch (CustomException e) {
            //프론트에 로그인 실패 에러를 하나로 통일해서 return 하기 위한 try-catch
            throw new CustomException(FAILED_TO_LOGIN);
        }

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
                .country(member.getUniversity().getCountry().getName())
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    @Transactional
    public TokenResponse reissueAccessToken(String refreshToken, String encryptedEmail) {

        TokenDto tokenDto = tokenService.verifyToken(refreshToken, false);
        tokenService.validateRefreshToken(refreshToken, encryptedEmail);

        CreatedTokenDto tokens = tokenService.createTokens(tokenDto.getMemberId(), tokenDto.getMemberRole(), encryptedEmail);

        return TokenResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    @Transactional
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        Member member = memberService.getByEmail(updatePasswordRequest.getEmail());
        authService.verifyCodeForChangePassword(member.getEmail(), updatePasswordRequest.getVerificationCode());
        memberService.updatePassword(member, updatePasswordRequest.getUpdatePassword());
        return new UpdatePasswordResponse(true);
    }

    @Transactional
    public SendVerificationCodeResponse sendVerificationCodeEmailForPwdReset(String email) {
        memberService.getByEmail(email); //check member exists by email
        return sendVerificationCodeEmail(email);
    }

    public PolicyResponse getPolicy(PolicyType policyType) {
        Policy policy = authService.getPolicy(policyType);
        return new PolicyResponse(policy.getContent());
    }

    @Transactional
    public void authenticateTempSignUp(Long memberId) {
        if (!tokenService.getTokenInfo().getMemberRole().equals(MemberRole.ADMIN)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }

        Member member = memberService.getById(memberId);
        member.authenticateTempMember();
        authService.sendTempSignUpEmail(member.getRowEmail(), TempSignUpStatusType.APPROVED);
        authService.sendTempSignUpNotification(member, TempSignUpStatusType.APPROVED);
    }

    @Transactional
    public String rejectTempSignUp(Long memberId) {
        if (!tokenService.getTokenInfo().getMemberRole().equals(MemberRole.ADMIN)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        Member member = memberService.getById(memberId);
        member.rejectTempSignUp();
        authService.sendTempSignUpEmail(member.getRowEmail(), TempSignUpStatusType.REJECTED);
        authService.sendTempSignUpNotification(member, TempSignUpStatusType.REJECTED);
        return "success";
    }

    public VerifyLoggedInMemberResponse getVerifiedMemberInfo() {
        Long memberIdByJwt = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberIdByJwt);
        return new VerifyLoggedInMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getUniversity().getName(),
                member.getUniversity().getCountry().getName()
        );
    }
}
