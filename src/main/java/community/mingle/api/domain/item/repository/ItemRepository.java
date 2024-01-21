package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
