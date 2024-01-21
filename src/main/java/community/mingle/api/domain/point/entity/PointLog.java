package community.mingle.api.domain.point.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Table(name = "point_log")
public class PointLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(name = "changed_amount", nullable = false)
    private Long changedAmount;

    @Size(max = 100)
    @NotNull
    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @NotNull
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}