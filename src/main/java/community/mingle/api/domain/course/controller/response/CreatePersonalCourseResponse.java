package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseTimeDto;

import java.util.List;

public record CreatePersonalCourseResponse(
        String name,
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue
) {
}
