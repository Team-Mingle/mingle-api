package community.mingle.api.domain.friend.controller;

import community.mingle.api.domain.friend.controller.response.CreateFriendCodeResponse;
import community.mingle.api.domain.friend.facade.FriendFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendFacade friendFacade;

     @GetMapping("/code")
     public ResponseEntity<CreateFriendCodeResponse> createFriendCode() {
         return ResponseEntity.ok(friendFacade.createFriendCode());
     }

}
