package community.mingle.api.domain.course.entity;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.entitybase.AuditLoggingBase;
import community.mingle.api.global.exception.CustomException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static community.mingle.api.global.exception.ErrorCode.POINT_NOT_ENOUGH;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "point")
public class Point extends AuditLoggingBase {
    @Id
    @Column(name = "member_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    public void useAmount(Long amount) {
        if (this.amount <= amount) {
            throw new CustomException(POINT_NOT_ENOUGH);
        }
        this.amount -= amount;
    }

    public void addAmount(Long amount) {
        this.amount += amount;
    }

}