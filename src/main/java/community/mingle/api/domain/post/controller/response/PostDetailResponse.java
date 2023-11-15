package community.mingle.api.domain.post.controller.response;

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
    private final boolean isFileAttached;
    private final int likeCount;
    private final int scrapCount;
    private final int commentCount;
    private final boolean isMyPost;
    private final boolean isLiked;
    private final boolean isScraped;
    private final boolean isBlinded;
    private final boolean isReported;
    private final String createdAt;
    private final int viewCount;
    private final List<String> postImgUrl;
    private final boolean isAdmin;

}

