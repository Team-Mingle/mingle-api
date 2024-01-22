package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    long countByPostIdAndMemberId(Long postId, Long memberId);

    Optional<PostScrap> findByPostAndMember(Post post, Member member);

}
