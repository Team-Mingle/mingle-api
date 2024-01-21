package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCommentRepository extends JpaRepository<ItemComment, Long> {

    List<ItemComment> findAllByItemId(Long itemId);
}
