package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    long countByItemIdAndMemberId(Long id, Long memberId);

}
