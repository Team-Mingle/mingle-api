package community.mingle.api.domain.friend.repository;

import community.mingle.api.domain.friend.entity.FriendCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendCodeRepository extends JpaRepository<FriendCode, Long> {

    public Optional<FriendCode> findByCode(String code);

    @Query(value = "select fc.default_member_name from friend_code fc where fc.member_id = :memberId order by fc.created_at desc limit 1", nativeQuery = true)
    public Optional<String> findLastDefaultMemberName(Long memberId);
}
