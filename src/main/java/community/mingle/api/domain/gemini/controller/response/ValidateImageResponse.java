package community.mingle.api.domain.gemini.controller.response;

import jakarta.validation.constraints.NotBlank;


public record ValidateImageResponse(
        @NotBlank
        String response
) {
}
