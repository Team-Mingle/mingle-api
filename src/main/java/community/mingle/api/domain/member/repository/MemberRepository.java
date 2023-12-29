package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository <Member, Long> {
    Optional<Member> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    List<Member> findAllByUniversityCountry(Country country);

    List<Member> findAllByUniversity(University university);


}
