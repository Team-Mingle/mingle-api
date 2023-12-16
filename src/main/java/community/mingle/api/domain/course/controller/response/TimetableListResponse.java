package community.mingle.api.domain.course.controller.response;

import community.mingle.api.enums.Semester;

import java.util.List;
import java.util.Map;

public record TimetableListResponse(
        Map<Semester, List<TimetablePreviewResponse>> timetablePreviewResponseMap

) {
}
