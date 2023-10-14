package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.security.component.TokenHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenHandler tokenHandler;

    public TokenResult createTokens(Member member, String hashedEmail) {
        Long memberId = member.getId();
        MemberRole memberRole = member.getRole();
        String accessToken = tokenHandler.createAccessToken(memberId, memberRole);
        String refreshToken = tokenHandler.createRefreshToken(memberId, memberRole, hashedEmail);
        return new TokenResult(accessToken, refreshToken);
    }

    public record TokenResult(String accessToken, String refreshToken) {
    }
}
