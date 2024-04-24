package community.mingle.api.domain.point.controller;

import community.mingle.api.domain.point.facade.PointFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Point Controller", description = "포인트샵 관련 API")
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    @Operation(summary = "잔여 포인트 조회 API")
    @GetMapping()
    public ResponseEntity<RemainingPointResponse> getRemainingPointAmount() {
        return ResponseEntity.ok(pointFacade.getRemainingPointAmount());
    }
}
