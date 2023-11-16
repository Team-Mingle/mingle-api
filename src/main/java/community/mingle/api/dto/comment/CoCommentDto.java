package community.mingle.api.dto.comment;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CoCommentDto extends CommentDto {
    private final Long parentCommentId;
    private final String mention;
}
