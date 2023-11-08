package community.mingle.api.domain.post.controller.request;

import community.mingle.api.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreatePostRequest {

    @NotNull(message = "카테고리를 입력해주세요.")
    private CategoryType categoryType;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 45)
    private String title;

    @NotBlank(message = "본문을 입력해주세요.")
    private String content;

    @NotNull(message = "익명여부를 입력해주세요.")
    private Boolean isAnonymous;

    private List<MultipartFile> multipartFile;

}
