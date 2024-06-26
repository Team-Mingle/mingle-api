package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreateCourseEvaluationRequest;
import community.mingle.api.domain.course.controller.response.CourseEvaluationResponse;
import community.mingle.api.domain.course.facade.CourseEvaluationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Course Evaluation Controller", description = "강의평가 관련 API")
@RestController
@RequestMapping("/course-evaluation")
@RequiredArgsConstructor
public class CourseEvaluationController {

    private final CourseEvaluationFacade courseEvaluationFacade;

    @Operation(summary = "강의 평가 생성 API")
    @PostMapping("/create")
    public ResponseEntity<Void> createCourseEvaluation(
        @RequestBody
        CreateCourseEvaluationRequest request
    ) {
        courseEvaluationFacade.create(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강의 평가 리스트 API")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseEvaluationResponse> getCourseEvaluationList(
        @PathVariable
        Long courseId
    ) {
        CourseEvaluationResponse response = courseEvaluationFacade.getCourseEvaluationList(courseId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "내가 쓴 강의 평가 리스트 API")
    @GetMapping("/my")
    public ResponseEntity<CourseEvaluationResponse> getMyCourseEvaluationList() {
        CourseEvaluationResponse response = courseEvaluationFacade.getMyCourseEvaluationList();
        return ResponseEntity.ok().body(response);
    }
}
