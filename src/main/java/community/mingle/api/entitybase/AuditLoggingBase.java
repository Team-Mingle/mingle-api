package community.mingle.api.entitybase;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditLoggingBase {

    @NotNull
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
