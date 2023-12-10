package community.mingle.api.domain.course.controller.request;

import community.mingle.api.enums.CourseEvaluationRating;
import community.mingle.api.enums.Semester;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateCourseEvaluationRequest(
    @NotNull(message = "강의명을 선택해 주세요.")
    Long courseId,
    @NotNull(message = "수강시기를 선택해 주세요.")
    Semester semester,
    @Min(value = 15, message = "강의평은 15자 이상 작성해주세요.")
    String comment,
    @NotNull(message = "강의 추천 여부를 선택해 주세요.")
    CourseEvaluationRating rating
) {
}
