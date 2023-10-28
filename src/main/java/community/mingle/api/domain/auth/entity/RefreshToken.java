package community.mingle.api.domain.auth.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends AuditLoggingBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiry;

    public static RefreshToken createRefreshToken(String email, String token, LocalDateTime expiry) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.email = email;
        refreshToken.token = token;
        refreshToken.expiry = expiry;
        return refreshToken;
    }
}


