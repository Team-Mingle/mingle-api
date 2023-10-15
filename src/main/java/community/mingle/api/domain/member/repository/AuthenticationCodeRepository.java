package community.mingle.api.domain.member.repository;

import community.mingle.api.domain.member.entity.AuthenticationCode;

import org.springframework.data.jpa.repository.JpaRepository;



public interface AuthenticationCodeRepository extends JpaRepository <AuthenticationCode,Long> {

    AuthenticationCode findByEmail (String email);

}

