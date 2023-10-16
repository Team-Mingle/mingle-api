package community.mingle.api.domain.post.controller;

import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.response.CreatePostResponse;
import community.mingle.api.domain.post.facade.PostFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostFacade postFacade;

    /**
     * 1.3 이메일 입력 & 중복검사 API
     */
    @PostMapping("/{boardType}")
    public ResponseEntity<CreatePostResponse> createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest, @PathVariable Long boardType) {

        String response = authFacade.verifyEmail(postEmailRequest);
        return ResponseEntity.ok().body(response);

    }

}
