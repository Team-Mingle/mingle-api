package community.mingle.api.domain.course.controller.request;

public record UpdateTimetableCourseRequest(
        Long courseId,
        boolean overrideValidation
) {
}
