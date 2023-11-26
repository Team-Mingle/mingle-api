package community.mingle.api.domain.course.controller.request;

import community.mingle.api.dto.course.CourseTimeDto;

import java.util.List;

public record CreatePersonalCourseRequest(
        String name,
        List<CourseTimeDto> courseTimeDtoList,
        String courseCode,
        String venue,
        String professor,
        String memo
) {
}
