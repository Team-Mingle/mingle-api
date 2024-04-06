package community.mingle.api.domain.backoffice.controller;

import community.mingle.api.domain.auth.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> RejectTempSignUp(@RequestParam Long memberId) {
        authFacade.rejectTempSignUp(memberId);
        return ResponseEntity.ok().build();
    }


}
