package community.mingle.api.domain.course.controller.request;

import community.mingle.api.enums.DayOfWeek;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@ToString
public class CourseTimeRequest {
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
}