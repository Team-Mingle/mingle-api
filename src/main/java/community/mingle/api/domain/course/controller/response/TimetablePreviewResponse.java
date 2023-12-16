package community.mingle.api.domain.course.controller.response;

import community.mingle.api.enums.Semester;

public record TimetablePreviewResponse(
        Long timetableId,
        Semester semester,
        String name,
        int orderNumber,
        boolean isPinned
) {
}
