package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PostFacade {
    private final PostService postService;
    private final PostImageService postImageService;
    private final TokenService tokenService;
    private final CommentService commentService;

    /**
     * 게시물 카테고리 목록 조회
     */
    public List<PostCategoryResponse> getPostCategory(){
        TokenDto tokenInfo = tokenService.getTokenInfo();
        return postService.getPostCategory(tokenInfo.getMemberRole());
    }

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
        return CreatePostResponse.builder()
                .postId(post.getId())
                .build();
    }

    @Transactional
    public UpdatePostResponse updatePost(UpdatePostRequest updatePostRequest, BoardType boardType, Long postId) {

//        Long memberIdByJwt = jwtService.getUserIdx();
        Long memberIdByJwt = 1L;

        Post post = postService.updatePost(memberIdByJwt,
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

    @Transactional
    public String deletePost(Long postId) {

        //        Long memberIdByJwt = jwtService.getUserIdx();
        Long memberIdByJwt = 1L;

        postService.deletePost(memberIdByJwt, postId);
        commentService.deleteComment(postId);
        postImageService.deletePostImage(postId);

        String response = "게시물 삭제에 성공하였습니다";
        return response;

    }


    public PostListResponse getBestPost(BoardType boardType, Pageable pageable) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        Page<Post> postPage = postService.findBestPostWithMemberLikeComment(boardType, memberId, pageable);

        //TODO to be replaced with builder implemented in DEV-117
        List<PostResponse> postResponseList = postPage.stream()
                .map(p -> PostResponse.builder()
                        .postId(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .build())
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .boardName(boardType.name())
                .postResponseList(postResponseList)
                .build();
    }




    }
