package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.CategoryType;
import lombok.Getter;

@Getter
public class PostCategoryResponse {

    private final String categoryName;

    public PostCategoryResponse(CategoryType category) {
        this.categoryName = category.name();
    }

}
