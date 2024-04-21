package community.mingle.api.domain.comment.repository;

import community.mingle.api.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    //WARNING: 해당 메소드는 mentionId를 사용해서 삭제된 comment까지 조회 가능하므로, 절대 다른 곳에 임의로 사용하지 말 것.
    @Query(value = "select * from comment c where c.id = :mentionId", nativeQuery = true)
    Comment findCommentByMentionId(Long mentionId);

}
