package community.mingle.api.domain.member;

import community.mingle.api.domain.member.TokenService.TokenResult;
import community.mingle.api.domain.member.dto.LoginMemberRequest;
import community.mingle.api.domain.member.dto.LoginMemberResponse;
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
        memberService.setFcmToken(member, request.getFcmToken());

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
