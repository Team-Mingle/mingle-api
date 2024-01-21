package community.mingle.api.domain.friend.repository;

import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    public List<Friend> findAllByMember(Member member);

    public boolean existsFriendByMemberAndFriend(Member member, Member friend);

    public Optional<Friend> findByMemberAndFriend(Member member, Member friend);
}
