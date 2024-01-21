package community.mingle.api.domain.friend.controller.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFriendCodeRequest(
        @NotBlank(message = "친구에게 보여질 이름을 입력해주세요.")
        String myDisplayName
) {
}
