package community.mingle.api.dto.post;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import community.mingle.api.enums.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostPreviewDto {

    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private String createdAt;
    private BoardType boardType;
    private CategoryType categoryType;
    private MemberRole memberRole;
    private final ContentStatusType status;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private boolean isFileAttached;
    private boolean isBlinded;
}
