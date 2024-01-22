package community.mingle.api.domain.post.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostScrap;
import community.mingle.api.domain.post.repository.PostScrapRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.SCRAP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostScrapService {

    private final PostScrapRepository postScrapRepository;


    @Transactional
    public PostScrap createPostScrap(Post post, Member member) {
        PostScrap postScrap = PostScrap.builder()
                .post(post)
                .member(member)
                .build();
        return postScrapRepository.save(postScrap);
    }

    @Transactional
    public void deletePostScrap(Post post, Member member) {
        PostScrap postScrap = postScrapRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(SCRAP_NOT_FOUND));
        postScrapRepository.delete(postScrap);
    }


    public boolean isPostScraped(Long postId, Long memberId) {
        return postScrapRepository.countByPostIdAndMemberId(postId, memberId) > 0;
    }
}