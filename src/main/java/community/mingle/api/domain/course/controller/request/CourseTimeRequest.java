package community.mingle.api.domain.course.controller.request;

import lombok.Getter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@ToString
public class CourseTimeRequest {
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
}