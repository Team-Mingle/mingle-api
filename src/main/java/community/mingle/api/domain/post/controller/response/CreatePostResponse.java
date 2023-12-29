package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatePostResponse {
    private final long postId;
}
