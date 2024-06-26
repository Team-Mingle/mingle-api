package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.EmailRequest;
import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.request.SignUpRequest;
import community.mingle.api.domain.auth.controller.request.TempSignUpRequest;
import community.mingle.api.domain.auth.controller.request.UpdatePasswordRequest;
import community.mingle.api.domain.auth.controller.request.VerificationCodeRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.controller.response.PolicyResponse;
import community.mingle.api.domain.auth.controller.response.SendVerificationCodeResponse;
import community.mingle.api.domain.auth.controller.response.SignUpResponse;
import community.mingle.api.domain.auth.controller.response.UpdatePasswordResponse;
import community.mingle.api.domain.auth.controller.response.VerifyCodeResponse;
import community.mingle.api.domain.auth.controller.response.VerifyEmailResponse;
import community.mingle.api.domain.auth.controller.response.VerifyLoggedInMemberResponse;
import community.mingle.api.domain.auth.entity.Policy;
import community.mingle.api.domain.auth.service.AuthService;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.backoffice.controller.response.TempSignUpApplyListResponse;
import community.mingle.api.domain.backoffice.controller.response.TempSignUpApplyResponse;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import community.mingle.api.domain.member.service.MemberAuthPhotoService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.security.CreatedTokenDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberAuthPhotoType;
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
import static community.mingle.api.global.exception.ErrorCode.FAILED_TO_LOGIN;
import static community.mingle.api.global.exception.ErrorCode.MEMBER_ALREADY_EXIST;
import static community.mingle.api.global.exception.ErrorCode.MODIFY_NOT_AUTHORIZED;
import static community.mingle.api.global.exception.ErrorCode.NICKNAME_DUPLICATED;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final MemberService memberService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final S3Service s3Service;
    private final MemberAuthPhotoService memberAuthPhotoService;

    //HKU, HKUST, CITYU, POLYU, NUS, NTU
    private final List<Integer> courseEvaluationAllowed = List.of(1, 2, 4, 5, 7, 8);

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
        if (memberService.existsByEmail(request.email())) {
            throw new CustomException(MEMBER_ALREADY_EXIST);
        }
        if (memberService.existsByNickname(request.nickname())) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }
        Member member = memberService.tempCreate(request.univId(), request.nickname(), request.email(), request.password(), request.fcmToken(), request.studentId());

        List<String> imgUrls = s3Service.uploadFile(request.multipartFile(), "temp_auth");
        imgUrls.forEach(imgUrl ->
                        memberAuthPhotoService.create(member.getId(), imgUrl, MemberAuthPhotoType.SIGNUP)
                );
        authService.sendTempSignUpEmail(request.email(), TempSignUpStatusType.PROCESSING, null);
        authService.sendTempSignUpEmail("team.mingle.aos@gmail.com", TempSignUpStatusType.ADMIN, null);
        authService.sendTempSignUpEmail("euler271@naver.com", TempSignUpStatusType.ADMIN, null);

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
        boolean isCourseEvaluationAllowed = courseEvaluationAllowed.contains(member.getUniversity().getId());

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .hashedEmail(member.getEmail())
                .nickName(member.getNickname())
                .univName(member.getUniversity().getName())
                .country(member.getUniversity().getCountry().getName())
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .isCourseEvaluationAllowed(isCourseEvaluationAllowed)
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
        memberAuthPhotoService.getById(memberId, MemberAuthPhotoType.SIGNUP).accepted();
        authService.sendTempSignUpEmail(member.getRawEmail(), TempSignUpStatusType.APPROVED, null);
        authService.sendTempSignUpNotification(member.getFcmToken(), TempSignUpStatusType.APPROVED);
        member.authenticateTempMember();
    }

    @Transactional
    public void rejectTempSignUp(Long memberId, String reason) {
        if (!tokenService.getTokenInfo().getMemberRole().equals(MemberRole.ADMIN)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        memberAuthPhotoService.getById(memberId, MemberAuthPhotoType.SIGNUP).rejected();
        Member member = memberService.getById(memberId);
        String memberFcmToken = member.getFcmToken();
        authService.sendTempSignUpEmail(member.getRawEmail(), TempSignUpStatusType.REJECTED, reason);
        authService.sendTempSignUpNotification(memberFcmToken, TempSignUpStatusType.REJECTED);
        member.withDraw();
    }

    public VerifyLoggedInMemberResponse getVerifiedMemberInfo() {
        Long memberIdByJwt = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberIdByJwt);
        boolean isCourseEvaluationAllowed = courseEvaluationAllowed.contains(member.getUniversity().getId());
        return new VerifyLoggedInMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getUniversity().getName(),
                member.getUniversity().getCountry().getName(),
                isCourseEvaluationAllowed
        );
    }

    public TempSignUpApplyListResponse getTempSignUpApplyList() {
        List<MemberAuthPhoto> photoList = memberAuthPhotoService.getUnauthenticatedSignupRequestPhotoList();
        List<TempSignUpApplyResponse> responses = photoList.stream().map(photo ->
                    new TempSignUpApplyResponse(
                        photo.getMember().getId(),
                        photo.getImageUrl(),
                        photo.getMember().getNickname(),
                        photo.getMember().getStudentId(),
                        photo.getMember().getUniversity().getName(),
                        photo.getMember().getRawEmail()
                    ))
            .toList();
        return new TempSignUpApplyListResponse(responses);
    }
}
