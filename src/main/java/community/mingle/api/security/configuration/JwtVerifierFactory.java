package community.mingle.api.security.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.infra.SecretsManagerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtVerifierFactory {

    private final SecretsManagerService secretsManagerService;
    public JWTVerifier jwtVerifier(boolean isAccessToken) {
        String jwtSecretKey;
        if (isAccessToken) {
           jwtSecretKey = secretsManagerService.getJwtSecretKey();
        } else jwtSecretKey = secretsManagerService.getRefreshJwtSecretKey();
        return JWT.require(Algorithm.HMAC256(jwtSecretKey))
                .withClaimPresence("memberId")
                .withClaimPresence("memberRole")
                .build();
    }
}
