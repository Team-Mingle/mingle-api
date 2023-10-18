package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.CreatePostResponse;
import community.mingle.api.domain.post.controller.response.UpdatePostResponse;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostFacade {
    private final PostService postService;
    private final PostImageService postImageService;

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, BoardType boardType) {
        //TODO 권한 validation
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
        return CreatePostResponse.builder()
                .postId(post.getId())
                .build();
    }

    @Transactional
    public UpdatePostResponse updatePost(UpdatePostRequest updatePostRequest, BoardType boardType, Long postId) {
        //TODO 권한 validation

//        Member member;
//        TotalPost totalPost;
//        Long memberIdByJwt = jwtService.getUserIdx();
//        member = postRepository.findMemberbyId(memberIdByJwt);

        Post post = postService.updatePost(
                                    postId,
                                    updatePostRequest.getTitle(),
                                    updatePostRequest.getContent(),
                                    updatePostRequest.isAnonymous());

        postImageService.updatePostImage(post, updatePostRequest.getImageIdsToDelete(), updatePostRequest.getImagesToAdd());

        return UpdatePostResponse.builder()
                        .postId(postId)
                        .categoryType(post.getCategoryType())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .isAnonymous(post.getAnonymous())
                        .build();


    }

}
