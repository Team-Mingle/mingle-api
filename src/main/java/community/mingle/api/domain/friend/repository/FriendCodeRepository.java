package community.mingle.api.domain.friend.repository;

import community.mingle.api.domain.friend.entity.FriendCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendCodeRepository extends JpaRepository<FriendCode, Long> {

    public Optional<FriendCode> findByCode(String code);
}
