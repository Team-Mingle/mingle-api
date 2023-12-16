package community.mingle.api.domain.friend.controller.response;

import community.mingle.api.dto.friend.FriendDto;

import java.util.List;

public record CreateFriendResponse(
        List<FriendDto> friendList
) {
}
