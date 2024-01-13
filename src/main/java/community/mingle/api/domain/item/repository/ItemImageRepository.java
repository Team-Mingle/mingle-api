package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findAllByItemId(Long itemId);
}
