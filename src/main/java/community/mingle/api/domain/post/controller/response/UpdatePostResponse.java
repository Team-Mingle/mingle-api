package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
public class UpdatePostResponse {

    private final Long postId;
    private final CategoryType categoryType;
    private final String title;
    private final String content;
    private final boolean isAnonymous;

}
