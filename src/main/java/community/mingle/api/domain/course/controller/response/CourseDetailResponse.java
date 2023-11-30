package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseTimeDto;

import java.util.List;

public record CourseDetailResponse(
        Long id,
        String name,
        String courseCode,
        String semester,
        List<CourseTimeDto> courseTimeDtoList,
        String venue,
        String professor,
        String subclass,
        String memo,
        String prerequisite
) {
}
