package community.mingle.api.domain.course.controller;

import community.mingle.api.domain.course.controller.request.AddCrawledCourseRequest;
import community.mingle.api.domain.course.service.InternalCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/internal-course")
@RequiredArgsConstructor
public class CourseInternalController {

    private final InternalCourseService internalCourseService;

    @PostMapping("/add-crawled-course")
    public ResponseEntity<Void> addCrawledCourse(
            @RequestBody AddCrawledCourseRequest request
    ) {
        internalCourseService.wow(request);

        return ResponseEntity.ok().build();
    }
}




