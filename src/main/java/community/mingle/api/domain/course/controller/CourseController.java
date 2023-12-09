package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.request.UpdatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.controller.response.CourseDetailResponse;
import community.mingle.api.domain.course.controller.response.CoursePreviewResponse;
import community.mingle.api.domain.course.facade.CourseFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Course Controller", description = "강의 관련 API")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseFacade courseFacade;

    @Operation(summary = "강의 직접 추가 API")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "TIMETABLE_CONFLICT - 시간표가 겹칩니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "COURSE_TIME_CONFLICT - 강의 시간이 겹치지 않게 설정해 주세요.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{timetableId}/personal")
    public ResponseEntity<CreatePersonalCourseResponse> createPersonalCourse(
            @RequestParam
            Long timetableId,
            @RequestBody CreatePersonalCourseRequest request
    ) {
        return ResponseEntity.ok(courseFacade.createPersonalCourse(timetableId, request));
    }

    @Operation(summary = "강의 상세 API")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseFacade.getCourseDetail(courseId));
    }

    @Operation(summary = "강의 검색 API")
    @GetMapping("/search")
    public ResponseEntity<List<CoursePreviewResponse>> searchCourse(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(courseFacade.searchCourse(keyword));
    }

    @Operation(summary = "강의 수정 API")
    @PatchMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> updatePersonalCourse(
            @PathVariable Long courseId,
            @RequestBody UpdatePersonalCourseRequest request
    ) {
        return ResponseEntity.ok(courseFacade.updateCourse(request, courseId));
    }




}
