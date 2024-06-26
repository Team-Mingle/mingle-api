package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.UpdatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CourseDetailResponse;
import community.mingle.api.domain.course.controller.response.CoursePreviewResponse;
import community.mingle.api.domain.course.facade.CourseFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Course Controller", description = "강의 관련 API")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseFacade courseFacade;

    @Operation(summary = "강의 상세 API")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseFacade.getCourseDetail(courseId));
    }

    @Operation(summary = "강의 검색 API")
    @GetMapping("/search")
    public ResponseEntity<CoursePreviewResponse> searchCourse(
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
