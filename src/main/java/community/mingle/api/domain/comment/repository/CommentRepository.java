package community.mingle.api.domain.comment.repository;

import community.mingle.api.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByPostId(Long postId);

}
