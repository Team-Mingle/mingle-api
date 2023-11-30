package community.mingle.api.domain.course.controller.request;

import community.mingle.api.dto.course.CourseTimeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePersonalCourseRequest(
        @NotBlank(message = "강의명을 입력해주세요.")
        String name,
        @Size(min = 1, message = "강의 시간을 입력해주세요.")
        @Size(max = 5, message = "강의 시간은 최대 5개까지 입력 가능합니다.")
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue,
        String professor,
        String memo
) {
}
