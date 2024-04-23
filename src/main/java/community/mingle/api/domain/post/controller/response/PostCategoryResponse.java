package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.CategoryType;
import lombok.Getter;

@Deprecated(since = "response 안에 categoryNameList 가 있도록 변경, use CategoryResponse instead")
@Getter
public class PostCategoryResponse {

    private final String categoryName;

    public PostCategoryResponse(CategoryType category) {
        this.categoryName = category.name();
    }

}
