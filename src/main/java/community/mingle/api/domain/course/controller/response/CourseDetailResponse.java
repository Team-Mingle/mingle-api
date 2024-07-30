package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseTimeDto;
import community.mingle.api.enums.Semester;

import java.util.List;

public record CourseDetailResponse(
        Long id,
        String name,
        String courseCode,
        Semester semester,
        List<CourseTimeDto> courseTimeDtoList,
        String venue,
        String professor,
        String subclass,
        String memo,
        String prerequisite
) {
}
