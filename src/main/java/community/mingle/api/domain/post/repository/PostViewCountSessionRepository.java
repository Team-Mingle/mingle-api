package community.mingle.api.domain.post.repository;

import community.mingle.api.domain.post.entity.PostViewCountSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostViewCountSessionRepository extends JpaRepository<PostViewCountSession, Long> {
    public Optional<PostViewCountSession> findByMemberIdAndPostId(Long memberId, Long postId);

}
