package community.mingle.api.dto.course;

import community.mingle.api.enums.CourseType;
import lombok.NoArgsConstructor;

import java.util.List;

public record CoursePreviewDto(
        Long courseTimetableId, // courseTimetable의 venue, professor, subclass 등 detail을 수정할 때 필요
        Long id,
        String name,
        String courseCode,
        String semester,
        String professor,
        String subclass,
        List<CourseTimeDto> courseTimeDtoList,
        String venue,
        String rgb,
        CourseType courseType
) {
}
