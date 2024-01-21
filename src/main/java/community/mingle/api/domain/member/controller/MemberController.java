package community.mingle.api.domain.member.controller;

import community.mingle.api.domain.member.facade.MemberFacade;
import community.mingle.api.domain.post.controller.response.PostListResponse;
import community.mingle.api.domain.post.facade.PostFacade;
import community.mingle.api.enums.BoardType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "닉네임 수정 API")
    @PatchMapping(path = "/nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "2017", description = "중복된 닉네임입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public ResponseEntity<Void> updateNickname(@Parameter String nickname) {
        memberFacade.updateNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO 중복 제거
    @Operation(summary = "내가 쓴 글 조회 API")
    @GetMapping(path = "/{boardType}/posts")
    public ResponseEntity<PostListResponse> getMyPagePostList(@PathVariable BoardType boardType, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPagePostResponse = postFacade.getMyPagePostList(boardType, pageRequest);
        return new ResponseEntity<>(myPagePostResponse, HttpStatus.OK);
    }


    @Operation(summary = "내가 댓글 쓴 글 조회 API")
    @GetMapping(path = "/{boardType}/comments")
    public ResponseEntity<PostListResponse> getMyPageCommentList(@PathVariable BoardType boardType, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPageCommentResponse = postFacade.getMyPageCommentList(boardType, pageRequest);
        return new ResponseEntity<>(myPageCommentResponse, HttpStatus.OK);
    }


    @Operation(summary = "내가 스크랩한 글 조회 API")
    @GetMapping(path = "/{boardType}/scraps")
    public ResponseEntity<PostListResponse> getMyPageScrapList(@PathVariable BoardType boardType, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPageScrapResponse = postFacade.getMyPageScrapList(boardType, pageRequest);
        return new ResponseEntity<>(myPageScrapResponse, HttpStatus.OK);
    }

    @Operation(summary = "내가 좋아요한 글 조회 API")
    @GetMapping(path = "/{boardType}/likes")
    public ResponseEntity<PostListResponse> getMyPageLikeList(@PathVariable BoardType boardType, @Parameter Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse myPageLikeResponse = postFacade.getMyPageLikePostList(boardType, pageRequest);
        return new ResponseEntity<>(myPageLikeResponse, HttpStatus.OK);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        memberFacade.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
