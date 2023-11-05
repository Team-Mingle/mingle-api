package community.mingle.api.domain.post.controller;

import community.mingle.api.domain.comment.facade.CommentFacade;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.enums.BoardType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostFacade postFacade;
    private final CommentFacade commentFacade;


    /**
     * 카테고리 목록 조회 API
     */
    @Operation(summary = "카테고리 목록 조회 API")
    @GetMapping("/category")
    public ResponseEntity<List<PostCategoryResponse>> getPostCategory() {
        return new ResponseEntity<>(postFacade.getPostCategory(), HttpStatus.OK);
    }


    /**
     * 게시물 상세 API
     */
    @Operation(summary = "게시물 상세 API")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> postDetail(@PathVariable Long postId) {
        return new ResponseEntity<>(postFacade.getPostDetail(postId), HttpStatus.OK);
    }

    /**
     * 게시물 상세 - 댓글 API
     */
    @Operation(summary = "게시물 상세 - 댓글 API")
    @GetMapping("/{postId}/comment")
    public ResponseEntity<List<CommentResponse>> postDetailComments(@PathVariable Long postId) {
        return new ResponseEntity<>(commentFacade.getPostDetailComments(postId), HttpStatus.OK);
    }



    /**
     * 게시물 생성 API
     */
    @Operation(summary = "게시물 생성 API")
    @PostMapping("/{boardType}")
    public ResponseEntity<CreatePostResponse> createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest, @PathVariable(value = "boardType") BoardType boardType) {

        //TODO ENUM 대소문자 및 일치하는 값 없는 경우 예외처리
        CreatePostResponse createPostResponse = postFacade.createPost(createPostRequest, boardType);
        return ResponseEntity.ok().body(createPostResponse);

    }


    /**
     * 게시물 수정 API
     */
    @Operation(summary = "게시물 수정 API")
    @PatchMapping("/{boardType}/{postId}")
    public ResponseEntity<UpdatePostResponse> updatePost(@Valid @ModelAttribute UpdatePostRequest updatePostRequest, @PathVariable(value = "boardType") BoardType boardType, @PathVariable Long postId) {

        UpdatePostResponse updatePostResponse = postFacade.updatePost(updatePostRequest, boardType, postId);
        return ResponseEntity.ok().body(updatePostResponse);

    }

    /**
     * 게시물 삭제 API
     */
    @Operation(summary = "게시물 삭제 API")
    @PatchMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {

        String response = postFacade.deletePost(postId);

        return ResponseEntity.ok().body(response);
    }

}
