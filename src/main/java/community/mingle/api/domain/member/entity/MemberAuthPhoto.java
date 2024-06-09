package community.mingle.api.domain.member.entity;

import community.mingle.api.domain.member.service.MemberAuthPhotoService;
import community.mingle.api.enums.MemberAuthPhotoStatus;
import community.mingle.api.enums.MemberAuthPhotoType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member_auth_photo")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAuthPhoto {
    @Id
    @Column(name = "member_id", nullable = false)
    private Long id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @NotNull
    @Column(name = "auth_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberAuthPhotoType authType;

    @Nullable
    @Column(name = "auth_status", nullable = true)
    @Enumerated(EnumType.STRING)
    private MemberAuthPhotoStatus authStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void accepted() {
        this.authStatus = MemberAuthPhotoStatus.ACCEPTED;
    }

    public void rejected() {
        this.authStatus = MemberAuthPhotoStatus.REJECTED;
    }

}