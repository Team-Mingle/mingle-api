package community.mingle.api.domain.post.controller.response;

import java.util.List;

public record CategoryResponse(
        List<String> categoryNames
) {
}
