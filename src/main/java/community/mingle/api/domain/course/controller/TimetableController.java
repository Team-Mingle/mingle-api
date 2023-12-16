package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableCourseRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableNameRequest;
import community.mingle.api.domain.course.controller.response.CreateTimetableResponse;
import community.mingle.api.domain.course.controller.response.TimetableDetailResponse;
import community.mingle.api.domain.course.controller.response.TimetableListResponse;
import community.mingle.api.domain.course.controller.response.UpdateTimetableCourseResponse;
import community.mingle.api.domain.course.facade.TimetableFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Timetable Controller", description = "시간표 관련 API")
@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableFacade timetableFacade;

    @Operation(summary = "시간표 생성 API")
    @PostMapping()
    public ResponseEntity<CreateTimetableResponse> createTimetable(
            @RequestBody CreateTimetableRequest request
    ) {
        return ResponseEntity.ok(timetableFacade.createTimetable(request));
    }

    @Operation(summary = "시간표 강의 추가 API")
    @PostMapping("/{timetableId}/course")
    public ResponseEntity<UpdateTimetableCourseResponse> updateTimetableCourse(
            @PathVariable Long timetableId,
            @RequestBody UpdateTimetableCourseRequest request
    ) {
        return ResponseEntity.ok(timetableFacade.updateTimetableCourse(timetableId, request));
    }

    @Operation(summary = "시간표 강의 삭제 API")
    @DeleteMapping("/{timetableId}/course/{courseId}")
    public ResponseEntity<Void> deleteTimetableCourse(
            @PathVariable Long timetableId,
            @PathVariable Long courseId
    ) {
        timetableFacade.deleteTimetableCourse(timetableId, courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시간표 삭제 API")
    @DeleteMapping("/{timetableId}")
    public ResponseEntity<Void> deleteTimetable(
            @PathVariable Long timetableId
    ) {
        timetableFacade.deleteTimetable(timetableId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시간표 이름 수정 API")
    @PatchMapping("/{timetableId}/name")
    public ResponseEntity<Void> updateTimetableName(
            @PathVariable Long timetableId,
            @RequestBody UpdateTimetableNameRequest request
            ) {
        timetableFacade.updateTimetableName(timetableId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시간표 고정/고정 취소 API")
    @PatchMapping("/{timetableId}/pin")
    public ResponseEntity<Void> updateTimetablePin(
            @PathVariable Long timetableId
    ) {
        timetableFacade.convertPinStatus(timetableId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시간표 리스트 조회 API")
    @GetMapping()
    public ResponseEntity<TimetableListResponse> getTimetableList() {
        return ResponseEntity.ok(timetableFacade.getTimetableList());
    }

    @Operation(summary = "시간표 상세 API")
    @GetMapping("/{timetableId}")
    public ResponseEntity<TimetableDetailResponse> getTimetableDetail(
            @PathVariable Long timetableId
    ) {
        return ResponseEntity.ok(timetableFacade.getTimetableDetail(timetableId));
    }

}
