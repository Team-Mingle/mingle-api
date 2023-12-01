package community.mingle.api.domain.banner.request;

import community.mingle.api.domain.member.entity.University;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateBannerRequest {

    @NotEmpty(message = "한 개 이상의 파일을 선택해 주세요.")
    @Size(min = 1, max = 5, message = "1개 이상, 5개 이하의 파일을 선택해 주세요.")
    private List<MultipartFile> multipartFile;

    private String linkUrl;

    @NotNull(message = "업로드할 학교를 입력해주세요.")
    private int universityId;
}
