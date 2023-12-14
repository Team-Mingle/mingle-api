package community.mingle.api.domain.friend.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.friend.controller.request.CreateFriendCodeRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.service.FriendService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendFacade {

    private final FriendService friendService;
    private final TokenService tokenService;
    private final MemberService memberService;

    @Transactional
    public CreateFriendCodeResponse createFriendCode(CreateFriendCodeRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        FriendCode friendCode = friendService.createFriendCode(member, request.defaultMemberName());
        return new CreateFriendCodeResponse(friendCode.getCode());
    }
}
