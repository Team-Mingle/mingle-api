package community.mingle.api.domain.course.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateTimetableRequest(
        @Schema(format = "yyyy")
        @NotNull(message = "학기를 선택해 주세요.")
        int year,
        @Schema(allowableValues = {"1", "2"})
        @NotNull(message = "학기를 선택해 주세요.")
        int semester
) {
}
