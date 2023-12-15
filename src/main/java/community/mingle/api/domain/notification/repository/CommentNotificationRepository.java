package community.mingle.api.domain.notification.repository;

import community.mingle.api.domain.notification.entity.CommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {
}
