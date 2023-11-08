package community.mingle.api.domain.comment.service;


import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void deleteComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).get();
        commentRepository.deleteAll(comments);
    }

    @Transactional
    public Comment create(Long postId, Long memberId, Long parentCommentId, Long mentionCommentId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        checkValidity(post, parentCommentId, mentionCommentId);


    }

    private void checkValidity(Post post, Long parentCommentId, Long mentionCommentId) {
        Set<Long> commentIdList = post.getCommentList().stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
        if (parentCommentId != null) {
            if (!commentIdList.contains(parentCommentId)) {
                throw new CustomException(ErrorCode.FAIL_TO_CREATE_COMMENT);
            }
        }
        if (mentionCommentId != null) {
            if (!commentIdList.contains(mentionCommentId)) {
                throw new CustomException(ErrorCode.FAIL_TO_CREATE_COMMENT);
            }
        }
    }

    private Long calculateAnonymousId(Post post, Member member) {
        Optional<List<Comment>> comments = commentRepository.findAllByPostId(post.getId());
        if (comments.isEmpty()) {
            return 1L;
        }
        Optional<Comment> resultComment = comments.flatMap(list -> list.stream()
                .filter(c -> c.getMember().equals(member))
                .findFirst());

        if (resultComment.isPresent()) {
            return resultComment.get().getAnonymousId();
        }

        comments.


    }

}
