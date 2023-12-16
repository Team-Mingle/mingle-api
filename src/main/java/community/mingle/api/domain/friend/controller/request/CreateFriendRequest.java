package community.mingle.api.domain.friend.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateFriendRequest(
        @NotBlank(message = "친구 코드를 입력해주세요.")
        String friendCode,
        @NotBlank(message = "친구에게 보여질 이름을 입력해주세요.")
        @Schema(description = "친구에게 보여질 이름")
        String myDisplayName
) {
}
