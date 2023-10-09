package community.mingle.api.src.auth;

import community.mingle.api.domain.member.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AuthRepositoryTest {
    @Autowired
    AuthRepository authRepository;

    @Test
    void existsByEmail() {
        /*
        given
         */

        /*
        when
         */


        /*
        then
         */

    }

    @Test
    void findByEmail() {
    }
}