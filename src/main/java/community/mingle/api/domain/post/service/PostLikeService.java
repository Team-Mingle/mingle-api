package community.mingle.api.domain.post.service;

import community.mingle.api.domain.like.entity.PostLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostLikeRepository;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostLike create(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(POST_NOT_EXIST));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        return postLikeRepository.save(postLike);
    }

    @Transactional
    public void delete(Long postLikeId, Long memberId) {
        PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postLikeId, memberId)
                .orElseThrow(() -> new CustomException(LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);
    }

    public boolean isPostLiked(Long postId, Long memberId) {
        return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }


}
