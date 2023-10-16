package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public class UpdatePostResponse {

    private Long postId;
    private CategoryType categoryType;
    private String title;
    private String content;
    private boolean isAnonymous;
    private List<MultipartFile> multipartFile;

}
