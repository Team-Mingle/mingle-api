package community.mingle.api.domain.auth.facade;

import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.auth.service.TokenService.TokenResult;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacade {

    private final MemberService memberService;
    private final TokenService tokenService;


    /**
     * 로그인
     */
    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        //이메일 암호화
        String hashedEmail = EmailHasher.hashEmail(request.getEmail());

        //이메일, 비밀번호 확인 로직
        Member member = memberService.getMemberByEmail(hashedEmail);
        memberService.checkPassword(request.getPwd(), member.getPassword());

        //신고된 유저, 탈퇴한 유저 확인
        memberService.validateLoginMemberStatusIsActive(member);

        //토큰 생성
        TokenResult tokens = tokenService.createTokens(member, hashedEmail);

        //FCM 토큰 지정
        memberService.updateFcmToken(member, request.getFcmToken());

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .email(hashedEmail)
                .nickName(member.getNickname())
                .univName(member.getUniversity().getName())
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
