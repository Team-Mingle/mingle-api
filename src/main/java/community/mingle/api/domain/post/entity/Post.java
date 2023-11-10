package community.mingle.api.domain.post.entity;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.like.entity.PostLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Size(max = 45)
    @NotNull
    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous = false;

    @NotNull
    @Column(name = "file_attached", nullable = false)
    private Boolean fileAttached = false;

    @Column(name = "view_count")
    private int viewCount;

    @NotNull
    @Column(name = "board", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @NotNull
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatusType statusType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostScrap> postScraps = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostImage> postImages = new ArrayList<>();


    @Builder
    public Post(String title, String content, BoardType boardType, CategoryType categoryType, boolean anonymous, boolean fileAttached) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.anonymous = anonymous;
        this.fileAttached = fileAttached;
    }

    public void updatePost (String title, String content, boolean isAnonymous){
        this.title = title;
        this.content = content;
        this.anonymous = isAnonymous;
    }

    public void deletePost() {
        this.deletedAt = LocalDateTime.now();
        this.statusType = ContentStatusType.INACTIVE;
    }

    public void updateView() {
        this.viewCount += 1;
    }

}