package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.auth.entity.RefreshToken;
import community.mingle.api.domain.auth.repository.RefreshTokenRepository;
import community.mingle.api.dto.security.CreatedTokenDto;
import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.security.component.TokenHandler;
import community.mingle.api.security.component.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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


    @Transactional
    public CreatedTokenDto createTokens(Long memberId, MemberRole memberRole, String email) {
        String accessToken = tokenHandler.createAccessToken(memberId, memberRole);
        String refreshToken = tokenHandler.createRefreshToken(memberId, memberRole);
        saveRefreshToken(email, refreshToken, Duration.of(1460, ChronoUnit.DAYS));
        return new CreatedTokenDto(accessToken, refreshToken);
    }

    /**
     * JWT 유효성 검사
     */
    public TokenDto verifyToken(String token, boolean isAccessToken) {
        return tokenVerifier.verifyToken(token, isAccessToken);
    }

    /**
     * refresh token 유효성/DB 검사
     */
    public void validateRefreshToken(String token, String encryptedEmail) {
        Optional<RefreshToken> refreshTokenOptional = findToken(token, encryptedEmail);
        System.out.println("중요!!!!! 인크립티드이메일: " + encryptedEmail + "토큰!!!!: " + token + "time: " + LocalDateTime.now().toString());
        refreshTokenOptional.ifPresentOrElse(
                refreshToken -> {
                    if (LocalDateTime.now().isAfter(refreshToken.getExpiry())) {
                        throw new CustomException(TOKEN_EXPIRED);
                    }
//                    refreshTokenRepository.delete(refreshToken);
                },
                () -> {throw new CustomException(TOKEN_NOT_FOUND);}
        );
    }
    public Optional<RefreshToken> findToken(String token, String encryptedEmail) {
        return refreshTokenRepository.findByTokenAndEmail(token, encryptedEmail);
    }


    @Transactional
    public void saveRefreshToken(String email, String token, Duration duration) {
        LocalDateTime expiry = LocalDateTime.now().plus(duration);

        refreshTokenRepository.findById(email)
                .ifPresentOrElse(
                        existingToken -> existingToken.updateRefreshtoken(token, expiry),
                        () -> {
                            RefreshToken newRefreshToken = RefreshToken.builder()
                                    .email(email)
                                    .token(token)
                                    .expiry(expiry)
                                    .build();
                            refreshTokenRepository.save(newRefreshToken);
                        }
                );
    }


    public TokenDto getTokenInfo() {
        return (TokenDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
