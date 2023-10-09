package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Member,Long> {
    boolean existsByEmail (String email);
    Member findByEmail (String email);
}
