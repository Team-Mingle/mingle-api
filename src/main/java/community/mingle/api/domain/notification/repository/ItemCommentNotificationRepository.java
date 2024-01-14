package community.mingle.api.domain.notification.repository;

import community.mingle.api.domain.notification.entity.ItemCommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCommentNotificationRepository extends JpaRepository<ItemCommentNotification, Long> {
}
