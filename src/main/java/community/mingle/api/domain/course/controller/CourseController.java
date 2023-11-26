package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.CreatePersonalCourseRequest;
import community.mingle.api.domain.course.controller.response.CreatePersonalCourseResponse;
import community.mingle.api.domain.course.controller.response.GetCourseDetailResponse;
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

    @Operation(summary = "강의 직접 추가 API")
    @PostMapping("/personal")
    public ResponseEntity<CreatePersonalCourseResponse> createPersonalCourse(
            @RequestBody CreatePersonalCourseRequest request
    ) {
        return ResponseEntity.ok(courseFacade.createPersonalCourse(request));
    }

//    @Operation(summary = "강의 상세 API")
//    @PostMapping("/{courseId}")
//    public ResponseEntity<GetCourseDetailResponse> getCourseDetail(
//            @PathVariable Long courseId
//    ) {
//        return ResponseEntity.ok(courseFacade.getCourseDetail(courseId));
//    }

    
}
