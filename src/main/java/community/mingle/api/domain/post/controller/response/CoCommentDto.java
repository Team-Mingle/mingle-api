package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CoCommentDto extends CommentDto {
    private final Long parentCommentId;
    private final String mention;
}
