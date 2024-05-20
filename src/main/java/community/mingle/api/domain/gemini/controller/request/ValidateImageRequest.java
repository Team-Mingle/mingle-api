package community.mingle.api.domain.gemini.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


public record ValidateImageRequest(
        @NotBlank
        String regex,
        @NotNull
        MultipartFile multipartFile
) {
}
