package community.mingle.api.domain.post.controller.response;

import community.mingle.api.dto.post.PostPreviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostListResponse {
    private List<PostPreviewDto> data;
}
