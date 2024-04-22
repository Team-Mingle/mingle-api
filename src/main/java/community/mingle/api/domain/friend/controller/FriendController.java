package community.mingle.api.domain.friend.controller;

import community.mingle.api.domain.friend.controller.request.CreateFriendRequest;
import community.mingle.api.domain.friend.controller.request.CreateFriendCodeRequest;
import community.mingle.api.domain.friend.controller.request.UpdateFriendNameRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.controller.response.CreateFriendResponse;
import community.mingle.api.domain.friend.controller.response.FriendListResponse;
import community.mingle.api.domain.friend.facade.FriendFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Friend Controller", description = "친구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendFacade friendFacade;

    @Operation(summary = "친구코드 생성 API")
    @PostMapping("/code")
     public ResponseEntity<CreateFriendCodeResponse> createFriendCode(
             @RequestBody
             CreateFriendCodeRequest request
     ) {
         return ResponseEntity.ok(friendFacade.createFriendCode(request));
     }

    @Operation(summary = "친구 추가 API")
    @PostMapping("/create")
    public ResponseEntity<CreateFriendResponse> createFriend(
            @RequestBody
            CreateFriendRequest createFriendRequest
    ) {
        CreateFriendResponse createFriendResponse = friendFacade.createFriend(createFriendRequest);
        return ResponseEntity.ok(createFriendResponse);
    }

    @Operation(
            summary = "친구에게 보여질 기본 이름 가져오기 API",
            description = "가장 마지막으로 사용한 친구에게 보여질 이름을 가져옵니다."
    )
    @GetMapping("/display-name")
    public ResponseEntity<String> getMyLastDisplayName() {
        return ResponseEntity.ok(friendFacade.getMyLastDisplayName());
    }

    @Operation(summary = "친구 리스트 API")
    @GetMapping()
    public ResponseEntity<FriendListResponse> listFriends() {
        return ResponseEntity.ok(friendFacade.listFriends());
    }

    @Operation(summary = "친구 삭제 API")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable
            Long friendId
    ) {
        friendFacade.deleteFriend(friendId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 이름 변경 API")
    @PatchMapping("/{friendId}")
    public ResponseEntity<Void> updateFriendName(
            @PathVariable
            Long friendId,
            @RequestBody
            UpdateFriendNameRequest request
    ) {
        friendFacade.updateFriendNameRequest(friendId, request.friendName());
        return ResponseEntity.ok().build();
    }
}
