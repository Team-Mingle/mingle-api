package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.auth.entity.RefreshToken;
import community.mingle.api.domain.auth.repository.RefreshTokenRepository;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.security.component.TokenHandler;
import community.mingle.api.security.component.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static community.mingle.api.global.exception.ErrorCode.TOKEN_EXPIRED;
import static community.mingle.api.global.exception.ErrorCode.TOKEN_NOT_FOUND;

/**
 * 토큰 관련 서비스
 * create, verify token
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenHandler tokenHandler; //create
    private final TokenVerifier tokenVerifier; //verify
    private final RefreshTokenRepository refreshTokenRepository;


    public TokenResult createTokens(Long memberId, MemberRole memberRole, String email) {
        String accessToken = tokenHandler.createAccessToken(memberId, memberRole);
        String refreshToken = tokenHandler.createRefreshToken(memberId, memberRole, email);
        return new TokenResult(accessToken, refreshToken);
    }

    public record TokenResult(String accessToken, String refreshToken) {
    }

    /**
     * JWT 유효성 검사
     */
    public TokenDto verifyToken(String token) {
        return tokenVerifier.verifyToken(token);
    }

    /**
     * refresh token 유효성/DB 검사
     */
    @Transactional
    public void validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOptional = findToken(token);

        refreshTokenOptional.ifPresent(refreshToken -> {
            if (LocalDateTime.now().isAfter(refreshToken.getExpiry())) {
                refreshTokenRepository.delete(refreshToken);
                throw new CustomException(TOKEN_EXPIRED);
            }
        });
        refreshTokenOptional.orElseThrow(() -> new CustomException(TOKEN_NOT_FOUND));
    }

    public Optional<RefreshToken> findToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void saveRefreshToken(String email, String token, Duration duration) {
        LocalDateTime expiry = LocalDateTime.now().plus(duration);

//        RefreshToken refreshToken = RefreshToken.createRefreshToken(email, token, expiry);
//        refreshTokenRepository.save(refreshToken);
    }

}
