package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    long countByPostIdAndMemberId(Long postId, Long memberId);

}
