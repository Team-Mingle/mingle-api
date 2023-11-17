package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
public class DeletePostLikeResponse {
    private final Boolean deleted;
}
