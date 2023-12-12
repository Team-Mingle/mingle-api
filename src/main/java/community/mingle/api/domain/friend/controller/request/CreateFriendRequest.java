package community.mingle.api.domain.friend.controller.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFriendRequest(
        @NotBlank(message = "친구 코드를 입력해주세요.")
        String friendCode,
        @NotBlank(message = "친구 이름을 입력해주세요.")
        String friendName
) {
}
