package community.mingle.api.domain.course.controller.request;

import community.mingle.api.dto.course.CourseTimeDto;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreatePersonalCourseRequest(
        @NotBlank(message = "강의명을 입력해주세요.")
        String name,
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue,
        String professor,
        String memo
) {
}
