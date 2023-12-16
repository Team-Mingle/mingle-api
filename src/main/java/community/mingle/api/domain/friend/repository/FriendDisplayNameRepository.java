package community.mingle.api.domain.friend.repository;

import community.mingle.api.domain.friend.entity.FriendDisplayName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDisplayNameRepository extends JpaRepository<FriendDisplayName, Long> {
}
