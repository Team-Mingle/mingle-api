package community.mingle.api.dto.course;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CourseTimeDto(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
