package community.mingle.api.domain.post.controller.response;

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
