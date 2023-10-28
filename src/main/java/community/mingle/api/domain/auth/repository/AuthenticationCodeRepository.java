package community.mingle.api.domain.auth.repository;

import community.mingle.api.domain.auth.entity.AuthenticationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthenticationCodeRepository extends JpaRepository <AuthenticationCode, Long> {

    Optional<AuthenticationCode> findByEmail (String email);

}

