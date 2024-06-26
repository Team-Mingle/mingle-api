package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.request.CreateTimetableRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableCourseRequest;
import community.mingle.api.domain.course.controller.request.UpdateTimetableNameRequest;
import community.mingle.api.domain.course.controller.response.*;
import community.mingle.api.domain.course.facade.CourseFacade;
import community.mingle.api.domain.course.facade.TimetableFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Timetable Controller", description = "시간표 관련 API")
@ApiResponses({
        @ApiResponse(responseCode = "200")
})
@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableFacade timetableFacade;
    private final CourseFacade courseFacade;


    @Operation(summary = "시간표 생성 API")
    @PostMapping()
    public ResponseEntity<CreateTimetableResponse> createTimetable(
            @RequestBody CreateTimetableRequest request
    ) {
        return ResponseEntity.ok(timetableFacade.createTimetable(request));
    }

    @Operation(summary = "시간표 학교 강의 추가 API")
    @PostMapping("/{timetableId}/course")
    public ResponseEntity<UpdateTimetableCourseResponse> updateTimetableCourse(
            @PathVariable Long timetableId,
            @RequestBody UpdateTimetableCourseRequest request
    ) {
        return ResponseEntity.ok(timetableFacade.updateTimetableCourse(timetableId, request));
    }

    @Operation(summary = "시간표 개인 강의 추가 API")
    @ApiResponse(responseCode = "409", description = "Conflict: \n" +
            "- TIMETABLE_CONFLICT: 시간표가 겹칩니다. \n" +
            "- COURSE_TIME_CONFLICT: 강의 시간이 겹치지 않게 설정해 주세요."
            , content = @Content(schema = @Schema(hidden = true))
    )
    @PostMapping("/{timetableId}/course/personal")
    public ResponseEntity<CreatePersonalCourseResponse> createPersonalCourse(
            @PathVariable
            Long timetableId,
            @RequestBody CreatePersonalCourseRequest request
    ) {
        return ResponseEntity.ok(courseFacade.createPersonalCourse(timetableId, request));
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

    @Operation(summary = "기본 시간표 변경 API")
    @PatchMapping("/{timetableId}/pin")
    public ResponseEntity<Void> changePinnedTimetable(
            @PathVariable Long timetableId
    ) {
        timetableFacade.changePinnedTimetable(timetableId);
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

    @Operation(summary = "친구 시간표 상세 리스트 API")
    @GetMapping("/friend/{friendId}")
    public ResponseEntity<FriendTimetableDetailResponse> getFriendTimetableList(
            @PathVariable Long friendId
    ) {
        return ResponseEntity.ok(timetableFacade.getFriendTimetableList(friendId));
    }

    @Operation(summary = "기본 시간표 Id 조회 API")
    @GetMapping("/default")
    public ResponseEntity<DefaultTimetableIdResponse> getDefaultTimetableId() {
        return ResponseEntity.ok(timetableFacade.getDefaultTimetableId());
    }



}
