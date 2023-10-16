package community.mingle.api.security.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class JwtVerifierConfiguration {

    private final Algorithm tokenAlgorithm;
    @Bean
    public JWTVerifier jwtVerifier() {
        return JWT.require(tokenAlgorithm)
                .withClaimPresence("memberId")
                .withClaimPresence("memberRole")
                .build();
    }
}
