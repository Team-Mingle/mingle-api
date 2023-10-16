package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.response.CreatePostResponse;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostFacade {
    private final PostService postService;
    private final PostImageService postImageService;

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, BoardType boardType) {
        boolean isFileAttached = (createPostRequest.getMultipartFile() != null) && (!createPostRequest.getMultipartFile().isEmpty());
        Post post = postService.createPost(
                                createPostRequest.getTitle(),
                                createPostRequest.getContent(),
                                boardType,
                                createPostRequest.getCategoryType(),
                                createPostRequest.getIsAnonymous(),
                                isFileAttached);
        if (isFileAttached) {
            postImageService.createPostImage(post, createPostRequest.getMultipartFile());
        }
    }

}
