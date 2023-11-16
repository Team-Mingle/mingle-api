package community.mingle.api.domain.post.controller.response;

import community.mingle.api.enums.ContentStatusType;
import community.mingle.api.enums.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostDetailResponse {

    private final Long postId;
    private final String title;
    private final String content;
    private final String nickname;
    private final String createdAt;
    private final MemberRole memberRole;
    private final ContentStatusType status;
    private final int likeCount;
    private final int commentCount;
    private final int viewCount;
    private final int scrapCount;
    private final boolean isFileAttached;
    private final boolean isBlinded;
    private final boolean isMyPost;
    private final boolean isLiked;
    private final boolean isScraped;
    private final List<String> postImgUrl;

}

