package community.mingle.api.domain.friend.controller;

import community.mingle.api.domain.friend.controller.request.CreateFriendRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.controller.response.CreateFriendResponse;
import community.mingle.api.domain.friend.facade.FriendFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Friend Controller", description = "친구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendFacade friendFacade;

     @GetMapping("/code")
     public ResponseEntity<CreateFriendCodeResponse> createFriendCode() {
         return ResponseEntity.ok(friendFacade.createFriendCode());
     }

    @PostMapping("/create")
    public ResponseEntity<CreateFriendResponse> createFriend(
            @RequestBody
            CreateFriendRequest createFriendRequest
    ) {
        CreateFriendResponse createFriendResponse = friendFacade.createFriend(createFriendRequest);
        return ResponseEntity.ok(createFriendResponse);
    }



}
