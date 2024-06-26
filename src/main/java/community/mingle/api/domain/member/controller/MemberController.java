package community.mingle.api.domain.member.controller;

import community.mingle.api.domain.item.controller.response.ItemListResponse;
import community.mingle.api.domain.item.facade.ItemFacade;
import community.mingle.api.domain.member.controller.request.ChangeNicknameRequest;
import community.mingle.api.domain.member.controller.request.FreshmanSignupRequest;
import community.mingle.api.domain.member.controller.request.WithdrawMemberRequest;
import community.mingle.api.domain.member.facade.MemberFacade;
import community.mingle.api.domain.post.controller.response.PostListResponse;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ItemStatusType;
import community.mingle.api.global.exception.CustomException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static community.mingle.api.global.exception.ErrorCode.INVALID_STATUS_REQUEST;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "MemberController", description = "회원 관련 API")
@ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                "- EMPTY_FIELD_ERROR: 필드 빈 값 입력 오류 \n" +
                "- REGEX_ERROR: 형식 (이메일, 비밀번호, 닉네임 등) 오류 \n" +
                "- SIZE_LIMIT_ERROR: 길이 제한 (비밀번호 등) 오류", content = @Content(schema = @Schema(hidden = true))
        )}
)
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final PostFacade postFacade;
    private final MemberFacade memberFacade;
    private final ItemFacade itemFacade;


    @Operation(summary = "닉네임 수정 API")
    @PatchMapping(path = "/nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "2017", description = "중복된 닉네임입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateNickname(@RequestBody ChangeNicknameRequest request) {
        memberFacade.updateNickname(request.newNickname());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO 중복 제거
    @Operation(summary = "내가 쓴 글 조회 API")
    @GetMapping(path = "/{boardType}/posts")
    public ResponseEntity<PostListResponse> getMyPagePostList(
            @PathVariable
            @Schema(allowableValues = {"TOTAL", "UNIV"})
            BoardType boardType,
            @Parameter Pageable pageable
    ) {
        if (boardType != BoardType.TOTAL && boardType != BoardType.UNIV) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPagePostResponse = postFacade.getMyPagePostList(boardType, pageRequest);
        return new ResponseEntity<>(myPagePostResponse, HttpStatus.OK);
    }


    @Operation(summary = "내가 댓글 쓴 글 조회 API")
    @GetMapping(path = "/{boardType}/comments")
    public ResponseEntity<PostListResponse> getMyPageCommentList(
            @PathVariable
            @Schema(allowableValues = {"TOTAL", "UNIV"})
            BoardType boardType,
            @Parameter Pageable pageable
    ) {
        if (boardType != BoardType.TOTAL && boardType != BoardType.UNIV) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPageCommentResponse = postFacade.getMyPageCommentList(boardType, pageRequest);
        return new ResponseEntity<>(myPageCommentResponse, HttpStatus.OK);
    }


    @Operation(summary = "내가 스크랩한 글 조회 API")
    @GetMapping(path = "/{boardType}/scraps")
    public ResponseEntity<PostListResponse> getMyPageScrapList(
            @PathVariable
            @Schema(allowableValues = {"TOTAL", "UNIV"})
            BoardType boardType,
            @Parameter Pageable pageable
    ) {
        if (boardType != BoardType.TOTAL && boardType != BoardType.UNIV) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPageScrapResponse = postFacade.getMyPageScrapList(boardType, pageRequest);
        return new ResponseEntity<>(myPageScrapResponse, HttpStatus.OK);
    }

    @Operation(summary = "내가 좋아요한 글 조회 API")
    @GetMapping(path = "/{boardType}/likes")
    public ResponseEntity<PostListResponse> getMyPageLikeList(
            @PathVariable
            @Schema(allowableValues = {"TOTAL", "UNIV"})
            BoardType boardType,
            @Parameter Pageable pageable
    ) {
        if (boardType != BoardType.TOTAL && boardType != BoardType.UNIV) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");

        PostListResponse myPageLikeResponse = postFacade.getMyPageLikePostList(boardType, pageRequest);
        return new ResponseEntity<>(myPageLikeResponse, HttpStatus.OK);
    }

    @Operation(summary = "내가 쓴 장터 게시물 조회 API")
    @GetMapping(path = "/items/{itemStatus}")
    public ResponseEntity<ItemListResponse> getMyPageItemList(
            @PathVariable
            @Schema(allowableValues = {"SELLING", "RESERVED", "SOLDOUT"})
            ItemStatusType itemStatus,
            @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        if (itemStatus != ItemStatusType.SELLING && itemStatus != ItemStatusType.RESERVED && itemStatus != ItemStatusType.SOLDOUT) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        ItemListResponse myPageItemResponse = itemFacade.getMyPageItemList(pageRequest, itemStatus);
        return new ResponseEntity<>(myPageItemResponse, HttpStatus.OK);
    }

    @Operation(summary = "내가 찜한 장터 게시물 조회 API")
    @GetMapping(path = "/item-likes/{itemStatus}")
    public ResponseEntity<ItemListResponse> getMyPageItemLikeList(
            @PathVariable
            @Schema(allowableValues = {"SELLING", "RESERVED", "SOLDOUT"})
            ItemStatusType itemStatus,
            @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        if (itemStatus != ItemStatusType.SELLING && itemStatus != ItemStatusType.RESERVED && itemStatus != ItemStatusType.SOLDOUT) {
            throw new CustomException(INVALID_STATUS_REQUEST);
        }
        ItemListResponse myPageItemLikeResponse = itemFacade.getMyPageItemLikeList(pageRequest, itemStatus);
        return new ResponseEntity<>(myPageItemLikeResponse, HttpStatus.OK);
    }


    @Operation(summary = "로그아웃 API")
    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        memberFacade.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴 API")
    @DeleteMapping(path = "/withdrawal")
    public ResponseEntity<Void> withdrawMember(@RequestBody @Valid WithdrawMemberRequest request) {
        memberFacade.withdraw(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "1.15 신입생 대상 재학생 인증 회원가입 api")
    @PatchMapping(path = "/freshman-sign-up")
    public ResponseEntity<Void> freshmanSignUp(
            @Valid @RequestBody FreshmanSignupRequest request
    ) {
        memberFacade.freshmanSignup(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
