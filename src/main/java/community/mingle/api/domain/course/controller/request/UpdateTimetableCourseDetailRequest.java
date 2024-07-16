package community.mingle.api.domain.course.controller.request;

public record UpdateTimetableCourseDetailRequest(
        String venue,
        String professor,
        String subclass
) {
}
