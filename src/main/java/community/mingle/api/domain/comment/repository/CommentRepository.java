package community.mingle.api.domain.comment.repository;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);
}
