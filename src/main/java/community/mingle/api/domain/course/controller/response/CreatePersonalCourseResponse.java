package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseTimeDto;

import java.util.List;

public record CreatePersonalCourseResponse(
        Long id,
        String name,
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue,
        String professor,
        String subclass,
        String rgb
) {
}
