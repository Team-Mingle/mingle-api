package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    long countByPostIdAndMemberId(Long postId, Long memberId);

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);
}
