package community.mingle.api.domain.member.entity;

import community.mingle.api.entitybase.AuditLoggingBase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="block_member")
public class BlockMember extends AuditLoggingBase {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long blockMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_member_id")
    private Member blockedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_member_id")
    private Member blockerMember;
}
