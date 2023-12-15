package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.notification.event.PopularPostNotificationEvent;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.like.entity.PostLike;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostLikeService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.post.PostStatusDto;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;


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
        commentService.deleteAllByPostId(postId);
        postImageService.deletePostImage(postId);

        return DeletePostResponse.builder()
                .deleted(true)
                .build();
    }

    public PostListResponse getPostList(BoardType boardType, CategoryType categoryType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.pagePostsByBoardTypeAndCategory(boardType, categoryType, pageRequest);

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


    public List<PostPreviewDto> getRecentPost(BoardType boardType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.getRecentPostList(boardType, memberId);

        return postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
    }


    @Transactional
    public CreatePostLikeResponse createPostLike(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        PostLike postLike = postLikeService.create(postId, memberId);
        if (postLike.getPost().getPostLikeList().size() == 5) {
            applicationEventPublisher.publishEvent(new PopularPostNotificationEvent(this, postId, memberId));
        }
        return new CreatePostLikeResponse(true);
    }

    @Transactional
    public DeletePostLikeResponse deletePostLike(Long postLikeId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        postLikeService.delete(postLikeId, memberId);
        return new DeletePostLikeResponse(true);
    }


    public PostListResponse getBestPost(PageRequest pageRequest) {

        Long memberId = tokenService.getTokenInfo().getMemberId();

        Page<Post> postPage = postService.getBestPostList(memberId ,pageRequest);

        List<PostPreviewDto> postPreviewDtoList = postPage.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);

    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.getPost(postId);

        postService.updateView(post);

        return mapToPostDetailResponse(post, memberId);
    }

    public PostListResponse getSearchPostList(String keyword, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.getPostByKeyword(keyword, memberId, pageRequest);

        List<PostPreviewDto> searchPostPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(searchPostPreviewDtoList);
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
                .boardType(post.getBoardType())
                .categoryType(post.getCategoryType())
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

    private PostPreviewDto mapToPostPreviewResponse(Post post, Long memberId) {
        PostStatusDto postStatusDto = postService.getPostStatus(post, memberId);
        String nickName = postService.calculateNickname(post);
        String title = postService.titleByStatus(post);
        String content = postService.contentByStatus(post);

        return PostPreviewDto.builder()
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
