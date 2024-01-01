package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CoursePreviewDto;

import java.util.List;

public record CoursePreviewResponse(
        List<CoursePreviewDto> data

        ) {
}
