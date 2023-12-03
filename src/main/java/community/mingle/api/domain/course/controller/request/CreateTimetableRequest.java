package community.mingle.api.domain.course.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateTimetableRequest(
        @Schema(format = "yyyy")
        @NotNull
        int year,
        @Schema(allowableValues = {"1", "2"})
        @NotNull
        int semester
) {
}
