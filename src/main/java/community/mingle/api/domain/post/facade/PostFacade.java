package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostLikeService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.post.PostStatusDto;
import community.mingle.api.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.api.global.utils.DateTimeConverter.convertToDateAndTime;

@RequiredArgsConstructor
@Transactional
@Service
public class PostFacade {
    private final PostService postService;
    private final PostImageService postImageService;
    private final PostLikeService postLikeService;
    private final TokenService tokenService;
    private final CommentService commentService;


    public List<PostCategoryResponse> getPostCategory(){
        MemberRole memberRole = tokenService.getTokenInfo().getMemberRole();

        return postService.getCategoryListByMemberRole(memberRole).stream()
                .map(PostCategoryResponse::new)
                .toList();
    }


    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest, BoardType boardType) {
        boolean isFileAttached = (createPostRequest.getMultipartFile() != null) && (!createPostRequest.getMultipartFile().isEmpty());
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.createPost(

                                memberId,
                                createPostRequest.getTitle(),
                                createPostRequest.getContent(),
                                boardType,
                                createPostRequest.getCategoryType(),
                                createPostRequest.getIsAnonymous(),
                                isFileAttached
        );
        if (isFileAttached) {
            postImageService.createPostImage(post, createPostRequest.getMultipartFile());
        }
        return CreatePostResponse.builder()
                .postId(post.getId())
                .build();
    }

    @Transactional
    public UpdatePostResponse updatePost(UpdatePostRequest updatePostRequest, Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();


        Post post = postService.updatePost(
                                    memberId,
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
    public DeletePostResponse deletePost(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        postService.deletePost(memberId, postId);
        commentService.deleteComment(postId);
        postImageService.deletePostImage(postId);

        return DeletePostResponse.builder()
                .deleted(true)
                .build();
    }

    public List<PostPreviewResponse> getPostList(BoardType boardType, CategoryType categoryType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.pagePostsByBoardTypeAndCategory(boardType, categoryType, pageRequest);

        return postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
    }


    public List<PostPreviewResponse> getRecentPost(BoardType boardType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.getRecentPostList(boardType, memberId);

        return postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
    }


    @Transactional
    public CreatePostLikeResponse createPostLike(Long postId, Long memberId) {
        postLikeService.create(postId, memberId);
        return new CreatePostLikeResponse(true);
    }


    public List<PostPreviewResponse> getBestPost(PageRequest pageRequest) {

        Long memberId = tokenService.getTokenInfo().getMemberId();

        Page<Post> postPage = postService.getBestPostList(memberId ,pageRequest);

        return postPage.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());

    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.getPost(postId);

        postService.updateView(post);

        return mapToPostDetailResponse(post, memberId);
    }

    private PostDetailResponse mapToPostDetailResponse(Post post, Long memberId) {
        PostStatusDto postStatusDto = postService.getPostStatus(post, memberId);
        List<String> imageUrls = postService.collectPostImageUrls(post);
        String nickName = postService.calculateNickname(post);
        String title = postService.titleByStatus(post);
        String content = postService.contentByStatus(post);

        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(title)
                .content(content)
                .nickname(nickName)
                .createdAt(convertToDateAndTime(post.getCreatedAt()))
                .memberRole(post.getMember().getRole())
                .status(post.getStatusType())
                .likeCount(post.getPostLikeList().size())
                .commentCount(postService.calculateActiveCommentCount(post))
                .viewCount(post.getViewCount())
                .scrapCount(post.getPostScrapList().size())
                .isFileAttached(post.getFileAttached())
                .isBlinded(false)
                .isMyPost(postStatusDto.isMyPost())
                .isLiked(postStatusDto.isLiked())
                .isScraped(postStatusDto.isScraped())
                .postImgUrl(imageUrls)
                .build();
    }

    private PostPreviewResponse mapToPostPreviewResponse(Post post, Long memberId) {
        PostStatusDto postStatusDto = postService.getPostStatus(post, memberId);
        String nickName = postService.calculateNickname(post);
        String title = postService.titleByStatus(post);
        String content = postService.contentByStatus(post);

        return PostPreviewResponse.builder()
                .postId(post.getId())
                .title(title)
                .content(content)
                .nickname(nickName)
                .viewCount(post.getViewCount())
                .createdAt(convertToDateAndTime(post.getCreatedAt()))
                .boardType(post.getBoardType())
                .memberRole(post.getMember().getRole())
                .categoryType(post.getCategoryType())
                .likeCount(post.getPostLikeList().size())
                .commentCount(post.getCommentList().size())
                .isFileAttached(post.getFileAttached())
                .isBlinded(postStatusDto.isBlinded())
                .build();
    }


}
