package community.mingle.api.security.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.infra.SecretsManagerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

import static community.mingle.api.global.exception.ErrorCode.FAILED_TO_CREATEJWT;

@Component
@AllArgsConstructor
public class TokenHandler {

    private final SecretsManagerService secretsManagerService;

    public String createAccessToken(Long memberId, MemberRole memberRole) {
        try {
            String jwtSecretKey = secretsManagerService.getJwtSecretKey();
            Date now = new Date();
            return JWT.create()
                    .withClaim("memberId", memberId)
                    .withClaim("memberRole", memberRole.toString())
                    .withExpiresAt(
                            //TODO: accessToken을 4년으로 둠
                            new Date(now.getTime() + 30 * 24 * 60 * 60 * 1000L)
                    )
                    .sign(Algorithm.HMAC256(jwtSecretKey));
        } catch (Exception e) {
            throw new CustomException(FAILED_TO_CREATEJWT);
        }
    }

    public String createRefreshToken(Long memberId, MemberRole memberRole) {
        try {
            String refreshJwtSecretKey = secretsManagerService.getRefreshJwtSecretKey();
            Date now = new Date();
            return JWT.create()
                    .withClaim("memberId", memberId)
                    .withClaim("memberRole", memberRole.toString())
                    .withExpiresAt(
                            new Date(now.getTime() + 1460 * 24 * 60 * 60 * 1000L)
                    )
                    .sign(Algorithm.HMAC256(refreshJwtSecretKey)
                    );
        } catch (Exception e) {
            throw new CustomException(FAILED_TO_CREATEJWT);
        }
    }


}
