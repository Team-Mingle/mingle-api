package community.mingle.api.domain.comment.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.post.controller.response.CoCommentDto;
import community.mingle.api.domain.post.controller.response.CommentDto;
import community.mingle.api.domain.post.controller.response.CommentResponse;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 게시물 상세 - 댓글 목록
     */
    public List<CommentResponse> getPostDetailComments(Long postId) {
        Post post = postService.getPost(postId);
        if (!postService.isValidPost(post)) return new ArrayList<>();
        Long memberIdByJwt = tokenService.getTokenInfo().getMemberId();
        List<CommentResponse> responseList = new ArrayList<>();

        Map<Comment, List<Comment>> commentListMap = commentService.getCommentsWithCoCommentsMap(postId, memberIdByJwt);
        Map<CommentDto, List<CoCommentDto>> commentDtoListMap = new HashMap<>();

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

    private CommentResponse createCommentResponse(CommentDto commentDto, List<CoCommentDto> coCommentDtoList) {
        return CommentResponse.builder()
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
