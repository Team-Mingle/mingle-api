package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemBlind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemBlindRepository extends JpaRepository<ItemBlind, Long> {

    List<ItemBlind> findByIdAndMemberId(Long id, Long memberId);
}
