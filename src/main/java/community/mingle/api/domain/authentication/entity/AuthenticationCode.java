package community.mingle.api.domain.authentication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "authentication_code")
@NoArgsConstructor
public class AuthenticationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "auth_code")
    private String authToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public AuthenticationCode(String email, String authToken) {
        this.email = email;
        this.authToken = authToken;
        this.createdAt = LocalDateTime.now();
    }


}
