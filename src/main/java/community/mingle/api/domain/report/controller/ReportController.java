package community.mingle.api.domain.report.controller;

import community.mingle.api.domain.report.controller.request.ReportRequest;
import community.mingle.api.domain.report.facade.ReportFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Report Controller", description = "신고 관련 API")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportFacade reportFacade;

    @Operation(summary = "신고 API")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "ALREADY_REPORTED - 이미 신고한 컨텐츠입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping
    public ResponseEntity<Void> report(@RequestBody @Valid ReportRequest request) {
        reportFacade.report(request);
        return ResponseEntity.ok().build();
    }



}
