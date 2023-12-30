package community.mingle.api.domain.course.controller.response;

import community.mingle.api.dto.course.CourseEvaluationDto;

import java.util.List;

public record CourseEvaluationResponse(
        List<CourseEvaluationDto> courseEvaluationList

) {
}
