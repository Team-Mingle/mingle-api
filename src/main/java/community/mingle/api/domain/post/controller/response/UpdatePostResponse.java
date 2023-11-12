package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.CategoryType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdatePostResponse {

    private final Long postId;
    private final CategoryType categoryType;
    private final String title;
    private final String content;
    private final boolean isAnonymous;

}
