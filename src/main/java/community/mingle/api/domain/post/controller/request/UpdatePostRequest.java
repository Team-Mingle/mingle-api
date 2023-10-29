package community.mingle.api.domain.post.controller.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdatePostRequest {
    private String title;
    private String content;
    private boolean isAnonymous;
    private List<Long> imageIdsToDelete;
    private List<MultipartFile> imagesToAdd;
}
