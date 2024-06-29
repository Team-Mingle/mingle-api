package community.mingle.api.domain.gemini.controller;

import com.google.cloud.vertexai.generativeai.ResponseHandler;
import community.mingle.api.domain.gemini.controller.request.ValidateImageRequest;
import community.mingle.api.domain.gemini.controller.response.ValidateImageResponse;
import community.mingle.api.domain.gemini.facade.GeminiFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiFacade geminiFacade;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ValidateImageResponse> validateImage(@ModelAttribute @Valid ValidateImageRequest request) throws IOException {
        return new ResponseEntity<>(new ValidateImageResponse(ResponseHandler.getText(this.geminiFacade.generateContentResponse(request))), HttpStatus.OK);
    }
}

