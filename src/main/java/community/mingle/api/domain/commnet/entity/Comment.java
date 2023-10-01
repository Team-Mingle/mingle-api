package community.mingle.api.domain.commnet.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.ContentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Getter
@Entity
@Table(name = "comment")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Comment extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "mention_id")
    private Long mentionId;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous = false;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatusType statusType;

    @Column(name = "deleted_at")
    private Instant deletedAt;

}