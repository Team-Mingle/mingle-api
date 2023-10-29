package community.mingle.api.domain.auth.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Ref;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken extends AuditLoggingBase {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiry", nullable = false)
    private LocalDateTime expiry;

    public void updateRefreshtoken(String token) {
        this.token = token;
        this.expiry = LocalDateTime.now().plusDays(30);
    }
}