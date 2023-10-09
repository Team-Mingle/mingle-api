package community.mingle.api.src.auth;

import community.mingle.api.domain.authentication.entity.AuthenticationCode;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodeRepository extends JpaRepository <AuthenticationCode,Long> {

    AuthenticationCode findByEmail (String email);

}

