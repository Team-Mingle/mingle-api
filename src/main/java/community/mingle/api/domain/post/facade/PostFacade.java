package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.like.entity.PostLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.notification.event.PopularPostNotificationEvent;
import community.mingle.api.domain.post.controller.request.CreatePostRequest;
import community.mingle.api.domain.post.controller.request.UpdatePostRequest;
import community.mingle.api.domain.post.controller.response.*;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.event.ReadPostEvent;
import community.mingle.api.domain.post.service.PostImageService;
import community.mingle.api.domain.post.service.PostLikeService;
import community.mingle.api.domain.post.service.PostScrapService;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.post.PostPreviewDto;
import community.mingle.api.dto.post.PostStatusDto;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.s3.S3Service;
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
    private final PostScrapService postScrapService;
    private final TokenService tokenService;
    private final CommentService commentService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final S3Service s3Service;
    private final MemberService memberService;
    private static final int POPULAR_NOTIFICATION_LIKE_SIZE = 5;


    public List<PostCategoryResponse> getPostCategory() {
        MemberRole memberRole = tokenService.getTokenInfo().getMemberRole();

        return postService.getCategoryListByMemberRole(memberRole).stream()
                .map(PostCategoryResponse::new)
                .toList();
    }


    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, BoardType boardType) {
        boolean isFileAttached = (request.getMultipartFile() != null) && (!request.getMultipartFile().isEmpty());
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.createPost(
                memberId,
                request.getTitle(),
                request.getContent(),
                boardType,
                request.getCategoryType(),
                request.getIsAnonymous(),
                isFileAttached
        );
        if (isFileAttached) {
            postImageService.createPostImage(post, request.getMultipartFile());
        }
        return CreatePostResponse.builder()
                .postId(post.getId())
                .build();
    }

    @Transactional
    public UpdatePostResponse updatePost(UpdatePostRequest request, Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.updatePost(
                memberId,
                postId,
                request.getTitle(),
                request.getContent(),
                request.isAnonymous()
        );
        postImageService.updatePostImage(post, request.getImageUrlsToDelete(), request.getImagesToAdd());

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

    public PostListResponse getAllPostList(BoardType boardType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.pagePostsByBoardType(boardType, pageRequest);

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


    public PostListResponse getPostList(BoardType boardType, CategoryType categoryType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.pagePostsByBoardTypeAndCategory(boardType, categoryType, pageRequest);

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


    public PostListResponse getRecentPost(BoardType boardType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.getRecentPostList(boardType, memberId);

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


    @Transactional
    public void updatePostLike(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        if (postLikeService.isPostLiked(postId, memberId)) {
            postLikeService.delete(postId, memberId);
        } else {
            PostLike postLike = postLikeService.create(postId, memberId);
            if (postLike.getPost().getPostLikeList().size() == POPULAR_NOTIFICATION_LIKE_SIZE) {
                applicationEventPublisher.publishEvent(new PopularPostNotificationEvent(this, postId, memberId));
            }
        }
    }

    @Transactional
    public void updatePostScrap(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Post post = postService.getPost(postId);

        if (postScrapService.isPostScraped(postId, memberId)) {
            postScrapService.deletePostScrap(post, member);
        } else {
            postScrapService.createPostScrap(post, member);
        }
    }


    public PostListResponse getBestPost(PageRequest pageRequest) {

        Long memberId = tokenService.getTokenInfo().getMemberId();

        Page<Post> postPage = postService.getBestPostList(memberId, pageRequest);

        List<PostPreviewDto> postPreviewDtoList = postPage.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);

    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Post post = postService.getPost(postId);

        applicationEventPublisher.publishEvent(
                new ReadPostEvent(this, postId, memberId)
        );

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


    /**
     * 마이페이지
     */
    public PostListResponse getMyPagePostList(BoardType boardType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Post> postList = postService.pagePostsByBoardTypeAndMember(boardType, member, pageRequest);
        if (postList.isEmpty()) return new PostListResponse(List.of());

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


    public PostListResponse getMyPageCommentList(BoardType boardType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Post> postList = postService.pageCommentPostsByBoardTypeAndMember(boardType, member, pageRequest);
        if (postList.isEmpty()) return new PostListResponse(List.of());

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }

    public PostListResponse getMyPageScrapList(BoardType boardType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Post> postList = postService.pageScrapPostsByBoardTypeAndMember(boardType, member, pageRequest);
        if (postList.isEmpty()) return new PostListResponse(List.of());

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }

    public PostListResponse getMyPageLikePostList(BoardType boardType, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Post> postList = postService.pageLikePostsByBoardTypeAndMember(boardType, member, pageRequest);
        if (postList.isEmpty()) return new PostListResponse(List.of());

        List<PostPreviewDto> postPreviewDtoList = postList.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());
        return new PostListResponse(postPreviewDtoList);
    }


}
