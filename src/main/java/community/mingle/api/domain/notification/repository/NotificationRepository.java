package community.mingle.api.domain.notification.repository;

import community.mingle.api.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
