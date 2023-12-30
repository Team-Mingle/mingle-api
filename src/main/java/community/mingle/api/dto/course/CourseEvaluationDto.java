package community.mingle.api.dto.course;

import community.mingle.api.enums.CourseEvaluationRating;
import community.mingle.api.enums.Semester;

public record CourseEvaluationDto(
        Long courseEvaluationId,
        Semester semester,
        String comment,
        CourseEvaluationRating rating
) {
}
