package community.mingle.api.domain.item.controller;

import community.mingle.api.domain.comment.controller.response.DeleteCommentResponse;
import community.mingle.api.domain.item.controller.request.CreateItemCommentRequest;
import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.item.controller.request.UpdateItemPostRequest;
import community.mingle.api.domain.item.controller.response.*;
import community.mingle.api.domain.item.facade.ItemCommentFacade;
import community.mingle.api.domain.item.facade.ItemFacade;
import community.mingle.api.domain.post.controller.response.PostDetailCommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Item Controller", description = "장터 관련 API")
@ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                "- EMPTY_FIELD_ERROR: 필드 빈 값 입력 오류 \n" +
                "- REGEX_ERROR: 형식 (이메일, 비밀번호, 닉네임 등) 오류 \n" +
                "- SIZE_LIMIT_ERROR: 길이 제한 (비밀번호 등) 오류", content = @Content(schema = @Schema(hidden = true))
        )}
)
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemFacade itemFacade;
    private final ItemCommentFacade itemCommentFacade;


    @Operation(summary = "장터 게시물 생성 API")
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateItemResponse> createItemPost(@ModelAttribute @Valid CreateItemRequest request) {
        CreateItemResponse createItemResponse = itemFacade.createItemPost(request);
        return new ResponseEntity<>(createItemResponse, HttpStatus.OK);
    }

    @Operation(summary = "장터 게시물 리스트 API")
    @GetMapping(path = "")
    public ResponseEntity<ItemListResponse> getItemPostList(@Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        ItemListResponse itemListResponse = itemFacade.getItemPostList(pageRequest);
        return new ResponseEntity<>(itemListResponse, HttpStatus.OK);
    }

    @Operation(summary = "장터 게시물 상세 API")
    @GetMapping(path = "/{itemId}")
    public ResponseEntity<ItemDetailResponse> getItemPost(@PathVariable Long itemId) {
        ItemDetailResponse itemDetailResponse = itemFacade.getItemPostDetail(itemId);
        return new ResponseEntity<>(itemDetailResponse, HttpStatus.OK);
    }


    @Operation(summary = "장터 게시물 댓글 조회 API")
    @GetMapping(path = "/{itemId}/comment")
    public ResponseEntity<List<PostDetailCommentResponse>> getItemPostComment(@PathVariable Long itemId) {
        List<PostDetailCommentResponse> commentListResponse = itemFacade.getItemComments(itemId);
        return new ResponseEntity<>(commentListResponse, HttpStatus.OK);
    }

    @Operation(summary = "장터 게시물 수정 API")
    @PatchMapping(path ="/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDetailResponse> updatePost(@Valid @ModelAttribute UpdateItemPostRequest request, @PathVariable Long itemId) {
        ItemDetailResponse response = itemFacade.updateItemPost(request, itemId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "장터 게시물 삭제 API")
    @DeleteMapping(path = "/{itemId}")
    public ResponseEntity<DeleteItemPostResponse> deletePost(@PathVariable Long itemId) {
        DeleteItemPostResponse response = itemFacade.deleteItemPost(itemId);
        return ResponseEntity.ok().body(response);
    }





    @Operation(summary = "장터 게시물 댓글 생성 API")
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CreateItemCommentResponse> create(@RequestBody @Valid CreateItemCommentRequest request) {
        CreateItemCommentResponse response = itemCommentFacade.createComment(request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "장터 게시물 댓글 삭제 API")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<DeleteItemCommentResponse> delete(@PathVariable Long commentId) {
        DeleteItemCommentResponse deleteCommentResponse = itemCommentFacade.delete(commentId);
        return new ResponseEntity<>(deleteCommentResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 좋아요/좋아요 취소 API")
    @PatchMapping("/comment/like/{commentId}")
    public ResponseEntity<Void> updateCommentLike(@PathVariable Long commentId) {
        itemCommentFacade.updateItemCommentLike(commentId);
        return ResponseEntity.ok().build();
    }


}
