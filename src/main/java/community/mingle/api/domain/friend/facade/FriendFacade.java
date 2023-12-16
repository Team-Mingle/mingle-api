package community.mingle.api.domain.friend.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.friend.controller.request.CreateFriendRequest;
import community.mingle.api.domain.friend.controller.request.CreateFriendCodeRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.controller.response.CreateFriendResponse;
import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.service.FriendService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.friend.FriendDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        FriendCode friendCode = friendService.createFriendCode(member, request.myDisplayName());
        return new CreateFriendCodeResponse(friendCode.getCode());
    }

    @Transactional
    public CreateFriendResponse createFriend(CreateFriendRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        friendService.createFriend(member, request.friendCode(), request.myDisplayName());
        List<Friend> friendList = friendService.listFriends(member);
        List<FriendDto> friendDtoList = friendList.stream()
                .map(friend -> new FriendDto(friend.getId(), friend.getName()))
                .toList();
        return new CreateFriendResponse(friendDtoList);
    }

    public String getMyLastDisplayName() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        return friendService.getMemberLastDisplayName(member);
    }
}
