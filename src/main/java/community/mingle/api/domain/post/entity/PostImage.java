package community.mingle.api.domain.post.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@SuperBuilder
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post_image SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "post_image")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

}