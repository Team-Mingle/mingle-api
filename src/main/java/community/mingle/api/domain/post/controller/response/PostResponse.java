package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int scrapCount;
    private int commentCount;
    private boolean isMyPost;
    private boolean isLiked;
    private boolean isScraped;
    private boolean isBlinded;
    private boolean isReported;
    private String createdAt;
    private int viewCount;
    private List<String> postImgUrl;
    private boolean isAdmin;

}

