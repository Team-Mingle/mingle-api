package community.mingle.api.domain.banner;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Where(clause = "deleted_at IS NULL")
@Table(name = "banner")
@Builder
@AllArgsConstructor
@SQLDelete(sql = "UPDATE banner SET deleted_at = CURRENT_TIMESTAMP, status = 'INACTIVE' WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends AuditLoggingBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @Column(name = "img_url", columnDefinition = "TEXT", nullable = false)
    private String imgUrl;

    @NotNull
    @Column(name = "link_url", columnDefinition = "TEXT", nullable = false)
    private String linkUrl;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatusType statusType;
}
