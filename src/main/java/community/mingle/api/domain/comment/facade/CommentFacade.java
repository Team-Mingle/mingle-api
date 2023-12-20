package community.mingle.api.domain.comment.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.controller.request.CreateCommentRequest;
import community.mingle.api.domain.comment.controller.response.CreateCommentLikeResponse;
import community.mingle.api.domain.comment.controller.response.CreateCommentResponse;
import community.mingle.api.domain.comment.controller.response.DeleteCommentLikeResponse;
import community.mingle.api.domain.comment.controller.response.DeleteCommentResponse;
import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.notification.event.CommentNotificationEvent;
import community.mingle.api.domain.comment.service.CommentLikeService;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.controller.response.PostDetailCommentResponse;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.comment.CoCommentDto;
import community.mingle.api.dto.comment.CommentDto;
import community.mingle.api.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static community.mingle.api.enums.ContentStatusType.INACTIVE;
import static community.mingle.api.enums.ContentStatusType.REPORTED;
import static community.mingle.api.global.utils.DateTimeConverter.convertToDateAndTime;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentFacade {

    private final PostService postService;
    private final TokenService tokenService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final MemberService memberService;

    private final ApplicationEventPublisher applicationEventPublisher;


    public List<PostDetailCommentResponse> getPostDetailComments(Long postId) { //TODO 성능 확인 (쿼리 N개)
        Post post = postService.getPost(postId);
        if (!postService.isValidPost(post)) return new ArrayList<>();
        Long memberIdByJwt = tokenService.getTokenInfo().getMemberId();
        List<PostDetailCommentResponse> responseList = new ArrayList<>();

        Map<Comment, List<Comment>> commentListMap = commentService.getCommentsWithCoCommentsMap(postId, memberIdByJwt);
        Map<CommentDto, List<CoCommentDto>> commentDtoListMap = new LinkedHashMap<>();

        commentListMap.forEach((comment, coCommentList) -> {
            CommentDto commentDto = createCommentDto(comment, memberIdByJwt, post.getMember().getId());
            List<CoCommentDto> coCommentDtos = coCommentList.stream()
                    .map(it -> createCoCommentDto(it, memberIdByJwt, post.getMember().getId()))
                    .toList();
            commentDtoListMap.put(commentDto, coCommentDtos);
        });
        commentDtoListMap.forEach((commentDto, coCommentDtoList) ->
                responseList.add(createCommentResponse(commentDto, coCommentDtoList)));

        return responseList;
    }

    @Transactional
    public CreateCommentResponse create(CreateCommentRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Comment comment = commentService.create(
                memberId,
                request.postId(),
                request.parentCommentId(),
                request.mentionId(),
                request.content(),
                request.isAnonymous()
        );
        Post post = postService.getPost(request.postId());
        Member member = memberService.getById(memberId);
        // 푸시알림 이벤트 발행
        applicationEventPublisher.publishEvent(
                new CommentNotificationEvent(this, post, comment, member, request.parentCommentId(), request.mentionId(), request.content())
        );
        return new CreateCommentResponse(comment.getId());
    }

    @Transactional
    public DeleteCommentResponse delete(Long commentId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        commentService.delete(commentId, memberId);
        return new DeleteCommentResponse(true);
    }

    @Transactional
    public CreateCommentLikeResponse createCommentLike(Long commentId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        commentLikeService.create(commentId, memberId);
        return new CreateCommentLikeResponse(true);
    }

    @Transactional
    public DeleteCommentLikeResponse deleteCommentLike(Long commentId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        commentLikeService.delete(commentId, memberId);
        return new DeleteCommentLikeResponse(true);
    }

    private PostDetailCommentResponse createCommentResponse(CommentDto commentDto, List<CoCommentDto> coCommentDtoList) {
        //TODO isAdmin -> role, isDeleted/isReported -> status 변경가능?
        return PostDetailCommentResponse.builder()
                .commentId(commentDto.getCommentId())
                .nickname(commentDto.getNickname())
                .content(commentDto.getContent())
                .likeCount(commentDto.getLikeCount())
                .isLiked(commentDto.isLiked())
                .isMyComment(commentDto.isMyComment())
                .isCommentFromAuthor(commentDto.isCommentFromAuthor())
                .isCommentDeleted(commentDto.isCommentDeleted())
                .isCommentReported(commentDto.isCommentReported())
                .createdAt(commentDto.getCreatedAt())
                .isAdmin(commentDto.isAdmin())
                .coCommentsList(coCommentDtoList)
                .build();
    }

    private CommentDto createCommentDto(Comment comment, Long memberId, Long postAuthorId) {
        //TODO isAdmin -> role, isDeleted/isReported -> status 변경가능?
        return CommentDto.builder()
                .commentId(comment.getId())
                .nickname(commentService.getDisplayName(comment, postAuthorId))
                .content(commentService.getContentByStatus(comment))
                .likeCount(comment.getCommentLikes().size())
                .isLiked(commentService.isCommentLikedByMember(comment, memberId))
                .isMyComment(memberId.equals(comment.getMember().getId()))
                .isCommentFromAuthor(postAuthorId.equals(comment.getMember().getId()))
                .isCommentDeleted(comment.getStatusType() == INACTIVE)
                .isCommentReported(comment.getStatusType() == REPORTED)
                .createdAt(convertToDateAndTime(comment.getCreatedAt()))
                .isAdmin(comment.getMember().getRole().equals(MemberRole.ADMIN))
                .build();
    }

    public CoCommentDto createCoCommentDto(Comment coComment, Long memberId, Long postAuthorId) {
        //TODO isAdmin -> role, isDeleted/isReported -> status 변경가능?
        return CoCommentDto.builder()
                .commentId(coComment.getId())
                .parentCommentId(coComment.getParentCommentId()) //
                .mention(commentService.getMentionDisplayName(coComment.getMentionId(), postAuthorId)) //
                .nickname(commentService.getDisplayName(coComment, postAuthorId))
                .content(commentService.getContentByStatus(coComment))
                .likeCount(coComment.getCommentLikes().size())
                .isLiked(commentService.isCommentLikedByMember(coComment, memberId))
                .isMyComment(memberId.equals(coComment.getMember().getId()))
                .isCommentFromAuthor(postAuthorId.equals(coComment.getMember().getId()))
                .isCommentDeleted(coComment.getStatusType() == INACTIVE)
                .isCommentReported(coComment.getStatusType() == REPORTED)
                .createdAt(convertToDateAndTime(coComment.getCreatedAt()))
                .isAdmin(coComment.getMember().getRole().equals(MemberRole.ADMIN))
                .build();
    }

}
