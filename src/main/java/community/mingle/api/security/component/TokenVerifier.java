package community.mingle.api.security.component;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import community.mingle.api.dto.security.DevTokenDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.infra.SecretsManagerService;
import community.mingle.api.security.configuration.JwtVerifierFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TokenVerifier {
    private final SecretsManagerService secretsManagerService;
    private final JwtVerifierFactory jwtVerifierFactory;


    public TokenDto verify(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            return null;
        }

        return verifyToken(authorizationHeader, true);
    }

    public TokenDto verifyToken(String bearerToken, boolean isAccessToken) {
        try {
            DevTokenDto devToken = secretsManagerService.getJwtDevToken();
            String token = extractBearerToken(bearerToken);

            if (token.equals(devToken.getMingleUser())) {
                return new TokenDto(1L, MemberRole.USER);
            } else if (token.equals(devToken.getMingleAdmin())) {
                return new TokenDto(2L, MemberRole.ADMIN);
            } else if (token.equals(devToken.getMingleKsa())) {
                return new TokenDto(3L, MemberRole.KSA);
            } else {
                return verifyIssuedToken(token, isAccessToken);
            }
        } catch (TokenExpiredException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException | IOException e) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    private TokenDto verifyIssuedToken(String token, boolean isAccessToken) {
        JWTVerifier tokenVerifier = jwtVerifierFactory.jwtVerifier(isAccessToken);
        DecodedJWT verifiedJwt = tokenVerifier.verify(token);
        return new TokenDto(verifiedJwt.getClaim("memberId").asLong(), MemberRole.valueOf(verifiedJwt.getClaim("memberRole").asString()));
    }

    private String extractBearerToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length());
        }
        return token;
    }
}
