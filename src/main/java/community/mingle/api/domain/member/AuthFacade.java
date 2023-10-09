package community.mingle.api.domain.member;

import community.mingle.api.domain.member.TokenService.TokenResult;
import community.mingle.api.domain.member.dto.LoginMemberRequest;
import community.mingle.api.domain.member.dto.LoginMemberResponse;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final MemberService memberService;
    private final TokenService tokenService;


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
        member.setFcmToken(request.getFcmToken()); // memberService.setFcmToken(request.getFcmToken()); //극한의 facade

        //Response 생성
        return loginMemberResponseBuilder(member, hashedEmail, tokens);
    }


    //TODO: too many arguments passed for ResponseBuilder -> 전용 메서드 또는 login 서비스 안에서 바로 해결
    private LoginMemberResponse loginMemberResponseBuilder(Member member, String hashedEmail, TokenResult tokens) {
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
