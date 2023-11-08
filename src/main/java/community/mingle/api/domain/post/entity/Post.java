package community.mingle.api.domain.post.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "post")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Size(max = 45)
    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous = false;

    @Column(name = "file_attached", nullable = false)
    private Boolean fileAttached = false;
    @Column(name = "view_count")
    private int viewCount;


    @Column(name = "board", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatusType statusType;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public void updatePost (String title, String content, boolean isAnonymous){
        this.title = title;
        this.content = content;
        this.anonymous = isAnonymous;
    }

    public void deletePost() {
        this.deletedAt = LocalDateTime.now();
        this.statusType = ContentStatusType.INACTIVE;
    }

}