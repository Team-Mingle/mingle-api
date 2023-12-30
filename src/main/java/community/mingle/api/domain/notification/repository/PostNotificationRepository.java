package community.mingle.api.domain.notification.repository;

import community.mingle.api.domain.notification.entity.PostNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostNotificationRepository extends JpaRepository<PostNotification, Long> {
}
