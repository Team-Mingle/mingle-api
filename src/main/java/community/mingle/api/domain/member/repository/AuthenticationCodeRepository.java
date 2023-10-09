package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.authentication.entity.AuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationCodeRepository extends JpaRepository <AuthenticationCode,Long> {

    AuthenticationCode findByEmail (String email);

}

