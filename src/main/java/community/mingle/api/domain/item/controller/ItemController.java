package community.mingle.api.domain.item.controller;

import community.mingle.api.domain.item.controller.request.CreateItemCommentRequest;
import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.item.controller.request.UpdateItemPostRequest;
import community.mingle.api.domain.item.controller.response.*;
import community.mingle.api.domain.item.facade.ItemCommentFacade;
import community.mingle.api.domain.item.facade.ItemFacade;
import community.mingle.api.domain.post.controller.response.PostDetailCommentResponse;
import community.mingle.api.enums.ItemStatusType;
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
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(hidden = true))),
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
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "REGEX_ERROR: 오픈채팅방 링크에 오류가 있습니다. 한번 더 확인해 주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "EMPTY_FIELD_ERROR: 오픈채팅방 링크를 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "EMPTY_FIELD_ERROR: 최소 한 장 이상의 사진을 첨부해 주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "SIZE_LIMIT_ERROR: 1개 이상, 5개 이하의 사진을 선택해 주세요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<CreateItemResponse> createItemPost(@ModelAttribute @Valid CreateItemRequest request) {
        CreateItemResponse createItemResponse = itemFacade.createItemPost(request);
        return new ResponseEntity<>(createItemResponse, HttpStatus.OK);
    }

    @Operation(summary = "장터 게시물 리스트 API")
    @GetMapping(path = "")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<ItemListResponse> getItemPostList(@Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        ItemListResponse itemListResponse = itemFacade.getItemPostList(pageRequest);
        return new ResponseEntity<>(itemListResponse, HttpStatus.OK);
    }

    @Operation(summary = "장터 게시물 상세 API")
    @GetMapping(path = "/{itemId}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
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
    @PatchMapping(path = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "REGEX_ERROR: 오픈채팅방 링크에 오류가 있습니다. 한번 더 확인해 주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "EMPTY_FIELD_ERROR: 오픈채팅방 링크를 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<ItemDetailResponse> updatePost(@Valid @ModelAttribute UpdateItemPostRequest request, @PathVariable Long itemId) {
        ItemDetailResponse response = itemFacade.updateItemPost(request, itemId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "장터 게시물 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "MODIFY_NOT_AUTHORIZED: 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @DeleteMapping(path = "/{itemId}")
    public ResponseEntity<DeleteItemPostResponse> deletePost(@PathVariable Long itemId) {
        DeleteItemPostResponse response = itemFacade.deleteItemPost(itemId);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary = "장터 게시물 찜/찜 취소 API")
    @PatchMapping("/like/{itemId}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateItemLike(@PathVariable Long itemId) {
        itemFacade.updateItemLike(itemId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "판매 상태 변경 API")
    @PatchMapping("/{itemId}/status/{itemStatusType}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "MODIFY_NOT_AUTHORIZED: 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateItemStatus(@PathVariable Long itemId, @PathVariable ItemStatusType itemStatusType) {
        itemFacade.updateItemStatus(itemId, itemStatusType);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장터 게시물 가리기/가리기 취소 API")
    @PatchMapping("/{itemId}/blind")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "MODIFY_NOT_AUTHORIZED: 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateItemBlind(@PathVariable Long itemId) {
        itemFacade.updateItemBlind(itemId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장터 게시물 검색 API")
    @GetMapping("/search")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<ItemListResponse> searchItem(@RequestParam(value = "keyword") String keyword, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        ItemListResponse searchItemList = itemFacade.getSearchItemList(keyword, pageRequest);
        return ResponseEntity.ok().body(searchItemList);
    }


    @Operation(summary = "장터 게시물 댓글 생성 API")
    @PostMapping("/{itemId}/comment")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "EMPTY_FIELD_ERROR: 댓글을 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "POST_NOT_EXIST: 게시물이 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "FAILED_TO_CREATE_COMMENT: 댓글 생성에 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<CreateItemCommentResponse> create(@RequestBody @Valid CreateItemCommentRequest request) {
        CreateItemCommentResponse response = itemCommentFacade.createComment(request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "장터 게시물 댓글 삭제 API")
    @DeleteMapping("/comment/{commentId}")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "MODIFY_NOT_AUTHORIZED: 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "COMMENT_NOT_FOUND: 존재하지 않는 멘션 댓글입니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<DeleteItemCommentResponse> delete(@PathVariable Long commentId) {
        DeleteItemCommentResponse deleteCommentResponse = itemCommentFacade.delete(commentId);
        return new ResponseEntity<>(deleteCommentResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 좋아요/좋아요 취소 API")
    @PatchMapping("/comment/like/{commentId}")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "MODIFY_NOT_AUTHORIZED: 수정 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "LIKE_NOT_FOUND: 좋아요를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateCommentLike(@PathVariable Long commentId) {
        itemCommentFacade.updateItemCommentLike(commentId);
        return ResponseEntity.ok().build();
    }


}
