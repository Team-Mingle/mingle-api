package community.mingle.api.dto.course;

import community.mingle.api.enums.CourseType;

import java.util.List;

public record CoursePreviewDto(
        Long id,
        String name,
        String courseCode,
        String semester,
        String professor,
        String subclass,
        List<CourseTimeDto> courseTimeDtoList,
        String venue,
        String rgb,
        CourseType courseType
) {
}
