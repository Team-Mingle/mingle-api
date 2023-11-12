package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeletePostResponse {
    private final boolean deleted;
}
