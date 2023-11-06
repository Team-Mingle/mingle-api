package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private String boardName;
    private List<PostResponse> postResponseList;
}
