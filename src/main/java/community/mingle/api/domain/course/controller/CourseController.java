package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.UpdatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CourseDetailResponse;
import community.mingle.api.domain.course.controller.response.CoursePreviewResponse;
import community.mingle.api.domain.course.facade.CourseFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

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
            @RequestParam String keyword,
            @RequestParam int year,
            @RequestParam int semester,
            @Parameter Pageable pageable
    ) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(courseFacade.searchCourse(keyword, year, semester, pageRequest));
    }

    @Operation(summary = "강의 평가용 강의 검색 API")
    @GetMapping("/course-evaluation/search")
    public ResponseEntity<CoursePreviewResponse> searchCourseForCourseEvaluation(
        @RequestParam String keyword,
        @Parameter Pageable pageable
    ) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "id");
        return ResponseEntity.ok(courseFacade.searchCourseForCourseEvaluation(keyword, pageRequest));
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
