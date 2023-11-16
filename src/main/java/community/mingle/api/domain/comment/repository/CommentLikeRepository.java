package community.mingle.api.domain.comment.repository;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.like.entity.CommentLike;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentAndMember(Comment comment, Member member);

    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
}
