package community.mingle.api.domain.friend.controller;

import community.mingle.api.domain.friend.controller.request.CreateFriendCodeRequest;
import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.facade.FriendFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
