package community.mingle.api.domain.comment.service;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.repository.CommentLikeRepository;
import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.like.entity.CommentLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.*;
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentLike create(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
        return commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void delete(Long commentId, Long memberId) {
        CommentLike commentLike = commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId).orElseThrow(() -> new CustomException(LIKE_NOT_FOUND));
        if (!commentLike.getMember().getId().equals(memberId)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        commentLikeRepository.delete(commentLike);
    }

    public boolean isCommentLiked(Long commentId, Long memberId) {
        return commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId);
    }
}


