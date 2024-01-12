package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.ItemComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCommentRepository extends JpaRepository<ItemComment, Long> {
}
