package community.mingle.api.domain.item.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE item_image SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "item_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
