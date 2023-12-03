package community.mingle.api.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CourseTimeDto(
        @NotNull(message = "요일을 입력해주세요.")
        DayOfWeek dayOfWeek,
        @NotNull(message = "시작 시간을 입력해주세요.")
        @Schema(type = "String", pattern = "HH:mm:SS")
        LocalTime startTime,
        @NotNull(message = "종료 시간을 입력해주세요.")
        @Schema(type = "String", pattern = "HH:mm:SS")
        LocalTime endTime
) {
}
