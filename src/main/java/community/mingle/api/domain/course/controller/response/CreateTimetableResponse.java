package community.mingle.api.domain.course.controller.response;

import community.mingle.api.enums.Semester;

public record CreateTimetableResponse(
    Long id,
    String name,
    Semester semester

) {
}
