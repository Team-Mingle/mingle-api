package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
