package community.mingle.api.domain.member.facade;

import community.mingle.api.domain.auth.service.AuthService;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.controller.request.FreshmanSignupRequest;
import community.mingle.api.domain.member.controller.request.WithdrawMemberRequest;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.global.amplitude.AmplitudeService;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static community.mingle.api.global.exception.ErrorCode.EMAIL_DOMAIN_MISMATCH;
import static community.mingle.api.global.exception.ErrorCode.FAILED_TO_LOGIN;
import static community.mingle.api.global.exception.ErrorCode.NICKNAME_DUPLICATED;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final PostService postService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final AuthService authService;
    private final AmplitudeService amplitudeService;


    @Transactional
    public void updateNickname(String nickname) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        if (memberService.existsByNickname(nickname)) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }
        member.updateNickname(nickname);

        amplitudeService.log(memberId, "updateNickname", null);
    }

    @Transactional
    public void logout() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        memberService.deleteRefreshToken(member);
        member.setFcmToken(null);
        amplitudeService.log(memberId, "logout", null);
    }

    @Transactional
    public void withdraw(WithdrawMemberRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member tokenMember = memberService.getById(memberId);

        Member member;
        try {
            member = memberService.getByEmail(request.email());
            authService.checkPassword(request.pwd(), member.getPassword());
            memberService.checkIsSameMemberAsTokenMember(tokenMember, member);
        } catch (CustomException e) {
            throw new CustomException(FAILED_TO_LOGIN);
        }

        authService.checkMemberStatusActive(member);
        memberService.deleteRefreshToken(member);
        member.withDraw();
        amplitudeService.log(memberId, "withdraw", null);
    }

    @Transactional
    public void freshmanSignup(FreshmanSignupRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member tokenMember = memberService.getById(memberId);

        String officialEmailDomain = tokenMember.getUniversity().getEmailDomain();
        String requestedEmailDomain = request.email().split("@")[1];
        if (!officialEmailDomain.equals(requestedEmailDomain)) {
            throw new CustomException(EMAIL_DOMAIN_MISMATCH);
        }

        memberService.updateEmail(tokenMember, request.email());
    }

}
