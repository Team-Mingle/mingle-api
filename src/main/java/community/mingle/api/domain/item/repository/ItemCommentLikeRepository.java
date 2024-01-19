package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCommentLikeRepository extends JpaRepository<ItemCommentLike, Long> {

    Optional<ItemCommentLike> findByItemCommentIdAndMemberId(Long commentId, Long memberId);
    boolean existsByItemCommentIdAndMemberId(Long commentId, Long memberId);

}