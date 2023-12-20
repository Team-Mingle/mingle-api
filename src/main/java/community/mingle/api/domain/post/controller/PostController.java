package community.mingle.api.domain.post.controller;

import community.mingle.api.domain.comment.facade.CommentFacade;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "게시물 리스트 API")
    @GetMapping("/{boardType}/{categoryType}")
    //TODO status에 따른 title, content 변경
    public ResponseEntity<PostListResponse> pagePosts(@PathVariable BoardType boardType, @PathVariable CategoryType categoryType, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse postPreviewList = postFacade.getPostList(boardType, categoryType, pageRequest);
        return ResponseEntity.ok().body(postPreviewList);
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
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<DeletePostResponse> deletePost(@PathVariable Long postId) {

        DeletePostResponse deletePostResponse = postFacade.deletePost(postId);

        return ResponseEntity.ok().body(deletePostResponse);
    }

    @Operation(summary = "베스트 게시판 조회 API")
    @GetMapping("/best")
    public ResponseEntity<PostListResponse> getBestPost(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse bestPostList = postFacade.getBestPost(pageRequest);
        return ResponseEntity.ok().body(bestPostList);
    }


    @Operation(summary = "최신 게시판 조회 API")
    @GetMapping("/{boardType}/recent")
    public ResponseEntity<List<PostPreviewDto>> getRecentPost(@PathVariable(value = "boardType") BoardType boardType) {

        List<PostPreviewDto> postPreviewResponseList = postFacade.getRecentPost(boardType);

        return ResponseEntity.ok().body(postPreviewResponseList);
    }


    @Operation(summary = "게시물 좋아요 생성 API")
    @PostMapping("/like/{postId}")
    public ResponseEntity<CreatePostLikeResponse> createPostLike(@PathVariable Long postId) {
        CreatePostLikeResponse createPostLikeResponse = postFacade.createPostLike(postId);
        return ResponseEntity.ok().body(createPostLikeResponse);
    }

    @Operation(summary = "게시물 좋아요 삭제 API")
    @DeleteMapping("/like/delete/{postId}")
    public ResponseEntity<DeletePostLikeResponse> deletePostLike(@PathVariable Long postId) {
        DeletePostLikeResponse deletePostLikeResponse = postFacade.deletePostLike(postId);

        return ResponseEntity.ok().body(deletePostLikeResponse);
    }


    @Operation(summary = "게시물 검색 API")
    @GetMapping("search")
    public ResponseEntity<PostListResponse> searchPost(@RequestParam(value = "keyword") String keyword, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse searchPostList = postFacade.getSearchPostList(keyword, pageRequest);
        return ResponseEntity.ok().body(searchPostList);

    }
}
