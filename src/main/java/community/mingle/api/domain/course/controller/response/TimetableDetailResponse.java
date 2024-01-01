package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CoursePreviewDto;
import community.mingle.api.enums.Semester;

import java.util.List;

public record TimetableDetailResponse(
    String name,
    Semester semester,
    List<CoursePreviewDto> coursePreviewDtoList

) {
}
