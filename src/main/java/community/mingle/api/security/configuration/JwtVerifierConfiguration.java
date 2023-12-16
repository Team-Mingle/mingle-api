package community.mingle.api.security.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.infra.SecretsManagerService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class JwtVerifierConfiguration {

    private final SecretsManagerService secretsManagerService;
    @Bean
    public JWTVerifier jwtVerifier() {
        String jwtSecretKey = secretsManagerService.getJwtSecretKey();
        return JWT.require(Algorithm.HMAC256(jwtSecretKey))
                .withClaimPresence("memberId")
                .withClaimPresence("memberRole")
                .build();
    }
}
