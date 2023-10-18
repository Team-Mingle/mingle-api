package community.mingle.api.src.auth;

import community.mingle.api.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

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