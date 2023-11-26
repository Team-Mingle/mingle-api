package community.mingle.api.domain.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Policy {
    @Id
    @Size(max = 45)
    @Column(name = "type", nullable = false, length = 45)
    private String type;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

}