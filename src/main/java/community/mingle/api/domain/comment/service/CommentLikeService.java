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

import static community.mingle.api.global.exception.ErrorCode.*;
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;

    public CommentLike create(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (commentLikeRepository.existsByCommentAndMember(comment, member)) {
            throw new CustomException(LIKE_ALREADY_EXIST);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
        return commentLikeRepository.save(commentLike);
    }
}
