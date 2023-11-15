package community.mingle.api.domain.post.controller;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.facade.CommentFacade;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.domain.post.service.PostLikeService;
import community.mingle.api.enums.BoardType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Post Controller", description = "게시물 관련 API")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostFacade postFacade;
    private final TokenService tokenService;
    private final CommentFacade commentFacade;


    @Operation(summary = "카테고리 목록 조회 API")
    @GetMapping("/category")
    public ResponseEntity<List<PostCategoryResponse>> getPostCategory() {
        return new ResponseEntity<>(postFacade.getPostCategory(), HttpStatus.OK);
    }

    @Operation(summary = "게시물 생성 API")
    @PostMapping(path = "/{boardType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePostResponse> createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest, @PathVariable(value = "boardType") BoardType boardType) {

        //TODO ENUM 대소문자 및 일치하는 값 없는 경우 예외처리
        CreatePostResponse createPostResponse = postFacade.createPost(createPostRequest, boardType);
        return new ResponseEntity<>(createPostResponse, HttpStatus.OK);
    }

    @Operation(summary = "게시물 상세 - 본문 API")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> postDetail(@PathVariable Long postId) {
        return new ResponseEntity<>(postFacade.getPostDetail(postId), HttpStatus.OK);
    }

    @Operation(summary = "게시물 상세 - 댓글 API")
    @GetMapping("/{postId}/comment")
    public ResponseEntity<List<PostDetailCommentResponse>> postDetailComments(@PathVariable Long postId) {
        return new ResponseEntity<>(commentFacade.getPostDetailComments(postId), HttpStatus.OK);
    }


    @Operation(summary = "게시물 수정 API")
    @PatchMapping(path ="/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatePostResponse> updatePost(@Valid @ModelAttribute UpdatePostRequest updatePostRequest, @PathVariable Long postId) {

        UpdatePostResponse updatePostResponse = postFacade.updatePost(updatePostRequest, postId);
        return ResponseEntity.ok().body(updatePostResponse);
    }


    @Operation(summary = "게시물 삭제 API")
    @PatchMapping("/delete/{postId}")
    public ResponseEntity<DeletePostResponse> deletePost(@PathVariable Long postId) {

        DeletePostResponse deletePostResponse = postFacade.deletePost(postId);

        return ResponseEntity.ok().body(deletePostResponse);
    }

    @Operation(summary = "베스트 게시판 조회 API")
    @GetMapping("/best")
    public ResponseEntity<List<PostPreviewResponse>> getBestPost(Pageable pageable) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        List<PostPreviewResponse> postPreviewResponseList = postFacade.getBestPost(pageRequest);

        return ResponseEntity.ok().body(postPreviewResponseList);
    }


    @Operation(summary = "최신 게시판 조회 API")
    @GetMapping("/{boardType}/recent")
    public ResponseEntity<List<PostPreviewResponse>> getRecentPost(@PathVariable(value = "boardType") BoardType boardType) {

        List<PostPreviewResponse> postPreviewResponseList = postFacade.getRecentPost(boardType);

        return ResponseEntity.ok().body(postPreviewResponseList);
    }


    @Operation(summary = "게시물 좋아요 생성")
    @PostMapping("/like/{postId}")
    public ResponseEntity<CreatePostLikeResponse> createPostLike(@PathVariable Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        CreatePostLikeResponse createPostLikeResponse = postFacade.createPostLike(postId, memberId);
        return ResponseEntity.ok().body(createPostLikeResponse);
    }

}
