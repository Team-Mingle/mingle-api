package community.mingle.api.domain.post.controller;

import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.CreatePostResponse;
import community.mingle.api.domain.post.controller.response.UpdatePostResponse;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.enums.BoardType;
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
     * 게시물 생성 API
     */
    @PostMapping("/{boardType}")
    public ResponseEntity<CreatePostResponse> createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest, @PathVariable(value = "boardType") BoardType boardType) {

        //TODO ENUM 대소문자 및 일치하는 값 없는 경우 예외처리
        CreatePostResponse createPostResponse = postFacade.createPost(createPostRequest, boardType);
        return ResponseEntity.ok().body(createPostResponse);

    }


    /**
     * 게시물 수정 API
     */
    @PatchMapping("/{boardType}/{postId}")
    public ResponseEntity<UpdatePostResponse> updatePost(@Valid @ModelAttribute UpdatePostRequest updatePostRequest, @PathVariable(value = "boardType") BoardType boardType, @PathVariable Long postId) {

        UpdatePostResponse updatePostResponse = postFacade.updatePost(updatePostRequest, boardType, postId);
        return ResponseEntity.ok().body(updatePostResponse);

    }

    /**
     * 게시물 삭제 API
     */
    @PatchMapping("/{boardType}/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable(value = "boardType") BoardType boardType, @PathVariable Long postId) {

        String response = postFacade.deletePost(postId);

        return ResponseEntity.ok().body(response);
    }

}
