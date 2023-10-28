package community.mingle.api.domain.auth.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken extends AuditLoggingBase {

    @Id
    @Column(length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiry;
}


