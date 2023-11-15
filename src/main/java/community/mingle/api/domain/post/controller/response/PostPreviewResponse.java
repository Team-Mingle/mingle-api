package community.mingle.api.domain.post.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostPreviewResponse {

    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private boolean isBlinded;
    private String createdAt;
    private int viewCount;
}
