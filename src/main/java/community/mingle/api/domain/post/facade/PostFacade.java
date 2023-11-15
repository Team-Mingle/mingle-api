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
import community.mingle.api.domain.post.service.PostService.PostStatusDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.*;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.api.global.exception.ErrorCode.POST_NOT_EXIST;
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

        return postService.getCategoriesByMemberRole(memberRole).stream()
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

    public List<PostPreviewResponse> getRecentPost(BoardType boardType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Post> postList = postService.findRecentPost(boardType, memberId);

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

        Page<Post> postPage = postService.findBestPosts(memberId ,pageRequest);

        return postPage.stream()
                .map(post -> mapToPostPreviewResponse(post, memberId))
                .collect(Collectors.toList());

    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        TokenDto tokenInfo = tokenService.getTokenInfo();
        Post post = postService.getPost(postId);
        PostStatusDto postStatusDto = postService.getPostStatus(post, tokenInfo.getMemberId());

        PostDetailResponse.PostDetailResponseBuilder basePostResponse = createBasePostDetailResponseBuilder(post);
        ContentStatusType postStatus = post.getStatusType();
        postService.updateView(post);

        return switch (postStatus) {
            case INACTIVE -> throw new CustomException(POST_NOT_EXIST);
            case DELETED -> buildDeletedPostResponse(basePostResponse);
            case REPORTED -> buildReportedPostResponse(basePostResponse, post);
            default -> buildDefaultPostResponse(basePostResponse, post, postStatusDto); // ACTIVE, NOTIFIED
        };
    }

    private PostDetailResponse.PostDetailResponseBuilder createBasePostDetailResponseBuilder(Post post) {
        String nickName = postService.calculateNickname(post);
        return PostDetailResponse.builder()
                .postId(post.getId())
                .nickname(nickName)
                .isFileAttached(post.getFileAttached())
                .viewCount(post.getViewCount())
                .isAdmin(post.getMember().getRole().equals(MemberRole.ADMIN))
                .isBlinded(false)
                .createdAt(convertToDateAndTime(post.getCreatedAt()));
    }

    private PostDetailResponse buildDefaultPostResponse(PostDetailResponse.PostDetailResponseBuilder builder, Post post, PostStatusDto postStatusDto) {
        List<String> imageUrls = postService.collectPostImageUrls(post);
        return builder.title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getPostLikeList().size())
                .scrapCount(post.getPostScrapList().size())
                .commentCount(postService.calculateActiveCommentCount(post))
                .postImgUrl(imageUrls)
                .isMyPost(postStatusDto.isMyPost())
                .isScraped(postStatusDto.isScraped())
                .isLiked(postStatusDto.isLiked())
                .isReported(false)
                .build();
    }

    private PostDetailResponse buildDeletedPostResponse(PostDetailResponse.PostDetailResponseBuilder builder) {
        return builder.title("운영규칙 위반에 따라 삭제된 글입니다.")
                .content("사유: 이용약관 제 12조 위반")
                .isReported(true)
                .build();
    }

    private PostDetailResponse buildReportedPostResponse(PostDetailResponse.PostDetailResponseBuilder builder, Post post) {
        ReportType reportType = postService.findReportedPostReason(post.getId(), ContentType.POST);
        return builder.title("다른 사용자들의 신고에 의해 삭제된 글 입니다.")
                .content("사유: " + reportType.getDescription())
                .isReported(true)
                .build();
    }

    private PostPreviewResponse mapToPostPreviewResponse(Post post, Long memberId) {
        PostStatusDto postStatusDto = postService.getPostStatus(post, memberId);
        String nickName = postService.calculateNickname(post);

        return PostPreviewResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(nickName)
                .isFileAttached(post.getFileAttached())
                .likeCount(post.getPostLikeList().size())
                .commentCount(post.getCommentList().size())
                .isBlinded(postStatusDto.isBlinded())
                .createdAt(convertToDateAndTime(post.getCreatedAt()))
                .viewCount(post.getViewCount())
                .build();
    }


}
