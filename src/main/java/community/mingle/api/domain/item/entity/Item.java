package community.mingle.api.domain.item.entity;

import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.enums.CurrencyType;
import community.mingle.api.enums.ItemStatusType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE item SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends AuditLoggingBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @NotNull
    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Size(max = 100)
    @NotNull
    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Lob
    @NotNull
    @Column(name = "chat_url", nullable = false)
    private String chatUrl;

    @NotNull
    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "status", nullable = false)
    private ItemStatusType status;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "item")
    private List<ItemComment> itemCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemImage> itemImageList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemLike> itemLikeList = new ArrayList<>();


    public static Item createItem(CreateItemRequest request, Member member) {
        Item item = new Item();
        item.member = member;
        item.title = request.title();
        item.price = request.price();
        item.currency = request.currencyType();
        item.content = request.content();
        item.location = request.location();
        item.chatUrl = request.chatUrl();
        item.anonymous = request.isAnonymous();
        item.status = ItemStatusType.SELLING;
        item.viewCount = 0;
        return item;
    }

    public void updateView() {
        this.viewCount += 1;
    }

    public Item updateItemPost(
            Long memberId,
            String title,
            String content,
            Long price,
            CurrencyType currency,
            String location,
            String chatUrl,
            boolean anonymous
    ) {
        if (!memberId.equals(this.member.getId())) {
            throw new CustomException(ErrorCode.MODIFY_NOT_AUTHORIZED);
        }
        this.title = title;
        this.content = content;
        this.price = price;
        this.currency = currency;
        this.location = location;
        this.chatUrl = chatUrl;
        this.anonymous = anonymous;
        return this;
    }
}
