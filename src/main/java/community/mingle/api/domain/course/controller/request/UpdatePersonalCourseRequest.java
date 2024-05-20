package community.mingle.api.domain.course.controller.request;

import community.mingle.api.dto.course.CourseTimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdatePersonalCourseRequest(
        @NotBlank(message = "강의명을 입력해주세요.")
        String name,
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue,
        String professor,
        String memo,
        @Schema(description = "true일 때 시간표 중복 검사를 하지 않습니다.")
        boolean overrideValidation
) {
}
