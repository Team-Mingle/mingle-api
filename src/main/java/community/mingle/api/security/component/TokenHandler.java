package community.mingle.api.security.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static community.mingle.api.global.exception.ErrorCode.FAILED_TO_CREATEJWT;

@Component
@AllArgsConstructor
public class TokenHandler {

    private final Algorithm tokenAlgorithm;

    public String createAccessToken(Long memberId, MemberRole memberRole) {
        try {
            return JWT.create()
                    .withClaim("memberId", memberId)
                    .withClaim("memberRole", memberRole.toString())
                    .withExpiresAt(
                            Date.from(
                                    LocalDateTime
                                            .now()
                                            .plusMinutes(30)
                                            .atZone(ZoneId.of("Asia/Seoul"))
                                            .toInstant()
                            )
                    )
                    .sign(tokenAlgorithm);
        } catch (Exception e) {
            throw new CustomException(FAILED_TO_CREATEJWT);
        }
    }


    public String createRefreshToken(Long memberId, MemberRole memberRole) {
        try {

            return JWT.create()
                    .withClaim("memberId", memberId)
                    .withClaim("memberRole", memberRole.toString())
                    .withExpiresAt(
                            Date.from(
                                    LocalDateTime
                                            .now()
                                            .plusDays(30)
                                            .atZone(ZoneId.of("Asia/Seoul"))
                                            .toInstant()
                            )
                    )
                    .sign(tokenAlgorithm);
        } catch (Exception e) {
            throw new CustomException(FAILED_TO_CREATEJWT);
        }
    }


}
