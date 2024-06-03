package community.mingle.api.domain.backoffice.controller;

import community.mingle.api.domain.auth.facade.AuthFacade;
import community.mingle.api.domain.backoffice.controller.request.RejectTempSignUpRequest;
import community.mingle.api.domain.backoffice.controller.response.TempSignUpApplyListResponse;
import community.mingle.api.domain.post.controller.response.PostListResponse;
import community.mingle.api.domain.post.facade.PostFacade;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Backoffice Controller", description = "백오피스 관련 API")
@ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                "- EMPTY_FIELD_ERROR: 필드 빈 값 입력 오류 \n" +
                "- REGEX_ERROR: 형식 (이메일, 비밀번호, 닉네임 등) 오류 \n" +
                "- SIZE_LIMIT_ERROR: 길이 제한 (비밀번호 등) 오류", content = @Content(schema = @Schema(hidden = true))
        )}
)
@RestController
@RequestMapping("/backoffice")
@RequiredArgsConstructor
public class BackofficeController {

    private final AuthFacade authFacade;
    private final PostFacade postFacade;

    @Operation(summary = "임시 회원가입 인증 api")
    @PostMapping("/authenticate-temp-sign-up")
    public ResponseEntity<Void> authenticateTempSignUp(
            @RequestParam Long memberId
    ) {
        authFacade.authenticateTempSignUp(memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "임시 회원가입 인증 불허가 api")
    @PostMapping("/reject-temp-sign-up")
    public ResponseEntity<Void> RejectTempSignUp(
        @RequestParam Long memberId,
        @RequestBody RejectTempSignUpRequest request
    ) {
        authFacade.rejectTempSignUp(memberId, request.rejectReason());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "임시 회원가입 요청 리스트 api")
    @GetMapping("/temp-sign-up-apply-list")
    public ResponseEntity<TempSignUpApplyListResponse> tempSignUpApplyList() {
        TempSignUpApplyListResponse response = authFacade.getTempSignUpApplyList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "국가별 광장 게시물 리스트 api")
    @GetMapping("/post/total/{countryId}/all")
    public ResponseEntity<PostListResponse> totalPostListByCountry(
    @PathVariable String countryId,
    @Parameter Pageable pageable
    ) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse response = postFacade.getTotalPostListByCountry(pageRequest, countryId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "대학별 잔디밭 게시물 리스트 api")
    @GetMapping("/post/univ/{universityId}/all")
    public ResponseEntity<PostListResponse> universityPostListByCountry(
            @PathVariable int universityId,
            @Parameter Pageable pageable
    ) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "createdAt");
        PostListResponse response = postFacade.getUniversityPostListByUniversityId(pageRequest, universityId);
        return ResponseEntity.ok().body(response);
    }
}
