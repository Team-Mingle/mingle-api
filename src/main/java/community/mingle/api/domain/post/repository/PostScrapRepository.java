package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.post.entity.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    long countByPostIdAndMemberId(Long postId, Long memberId);

}
