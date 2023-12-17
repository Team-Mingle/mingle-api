package community.mingle.api.domain.course.controller.response;

public record CoursePreviewResponse(
        Long id,
        String name,
        String courseCode,
        String semester,
        String professor,
        String subclass
) {
}
