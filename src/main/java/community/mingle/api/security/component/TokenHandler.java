package community.mingle.api.security.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import community.mingle.api.enums.MemberRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@AllArgsConstructor
public class TokenHandler {

    private final Algorithm tokenAlgorithm;

    //TODO RefreshToken도 redis를 뺄까?
    public String createAccessToken(Long memberId, MemberRole memberRole) {
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
    }

}
