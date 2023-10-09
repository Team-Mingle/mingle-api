package community.mingle.api.security.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.infra.SecretsManagerService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TokenAlgorithmConfiguration {

    private final SecretsManagerService secretsManagerService;

    @Bean
    public Algorithm tokenAlgorithm() {
        String jwtSecretKey = secretsManagerService.getJwtSecretKey();
        return Algorithm.HMAC256(jwtSecretKey);
    }
}
