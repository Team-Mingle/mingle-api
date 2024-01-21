package community.mingle.api.domain.member.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static community.mingle.api.global.exception.ErrorCode.NICKNAME_DUPLICATED;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final PostService postService;
    private final TokenService tokenService;
    private final MemberService memberService;


    @Transactional
    public void updateNickname(String nickname) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        if (memberService.existsByNickname(nickname)) {
            throw new CustomException(NICKNAME_DUPLICATED);
        }
        member.updateNickname(nickname);
    }
}
