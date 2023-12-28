package community.mingle.api.domain.friend.controller;

import community.mingle.api.domain.friend.controller.request.CreateFriendRequest;
import community.mingle.api.domain.friend.controller.request.CreateFriendCodeRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.controller.response.CreateFriendResponse;
import community.mingle.api.domain.friend.controller.response.FriendListResponse;
import community.mingle.api.domain.friend.facade.FriendFacade;
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

     @PostMapping("/code")
     public ResponseEntity<CreateFriendCodeResponse> createFriendCode(
             @RequestBody
             CreateFriendCodeRequest request
     ) {
         return ResponseEntity.ok(friendFacade.createFriendCode(request));
     }

    @PostMapping("/create")
    public ResponseEntity<CreateFriendResponse> createFriend(
            @RequestBody
            CreateFriendRequest createFriendRequest
    ) {
        CreateFriendResponse createFriendResponse = friendFacade.createFriend(createFriendRequest);
        return ResponseEntity.ok(createFriendResponse);
    }

    @GetMapping("/display-name")
    public ResponseEntity<String> getMyLastDisplayName() {
        return ResponseEntity.ok(friendFacade.getMyLastDisplayName());
    }

    @GetMapping()
    public ResponseEntity<FriendListResponse> listFriends() {
        return ResponseEntity.ok(friendFacade.listFriends());
    }




}
