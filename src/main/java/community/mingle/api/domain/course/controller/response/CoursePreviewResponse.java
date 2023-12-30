package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseTimeDto;

import java.util.List;

public record CoursePreviewResponse(
        Long id,
        String name,
        String courseCode,
        String semester,
        String professor,
        String subclass,
        List<CourseTimeDto> courseTimeDtoList,
        String rgb

        ) {
}
