package community.mingle.api.domain.gemini.facade;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import community.mingle.api.domain.gemini.controller.request.ValidateImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeminiFacade {

    private final GenerativeModel generativeModel;

    public GenerateContentResponse generateContentResponse(ValidateImageRequest request) throws IOException {
        return this.generativeModel.generateContent(
                ContentMaker.fromMultiModalData(PartMaker.fromMimeTypeAndData(
                        request.multipartFile().getContentType(), request.multipartFile().getBytes()),
                        String.format("What is the string with full match to regex %s", request.regex())));
    }
}

