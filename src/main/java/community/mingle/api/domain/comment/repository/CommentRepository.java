package community.mingle.api.domain.comment.repository;

import community.mingle.api.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    @Query("select c from Comment c join c.post as post " +
            "where post.id = :id and c.parentCommentId is null " +
            "and c.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) " +
            "order by c.createdAt asc")
    List<Comment> findComments(@Param("id") Long id, @Param("memberIdByJwt") Long memberIdByJwt);

    @Query("select c from Comment c join c.post as post " +
            "where post.id = :id and c.parentCommentId is not null " +
            "and c.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) " +
            "order by c.createdAt asc")
    List<Comment> findCoComments(@Param("id") Long id, @Param("memberIdByJwt") Long memberIdByJwt);

}
