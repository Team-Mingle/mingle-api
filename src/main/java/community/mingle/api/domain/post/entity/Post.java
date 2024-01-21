package community.mingle.api.domain.post.entity;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.like.entity.PostLike;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CategoryType;
import community.mingle.api.enums.ContentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP, status = 'INACTIVE' WHERE id = ?")
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

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostScrap> postScrapList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostImage> postImageList = new ArrayList<>();

    public void updatePost (String title, String content, boolean isAnonymous){
        this.title = title;
        this.content = content;
        this.anonymous = isAnonymous;
    }

//    public void deletePost() {
//        this.deletedAt = LocalDateTime.now();
//        this.statusType = ContentStatusType.INACTIVE;
//    }

    public void updateView() {
        this.viewCount += 1;
    }
    public void updateStatusAsNotified() {
        this.statusType = ContentStatusType.NOTIFIED;
    }

}