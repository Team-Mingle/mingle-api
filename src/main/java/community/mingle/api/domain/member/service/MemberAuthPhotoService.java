package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import community.mingle.api.domain.member.repository.MemberAuthPhotoRepository;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.enums.MemberAuthPhotoStatus;
import community.mingle.api.enums.MemberAuthPhotoType;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberAuthPhotoService {

    private final MemberAuthPhotoRepository memberAuthPhotoRepository;
    private final MemberRepository memberRepository;

    public MemberAuthPhoto create(Long memberId, String imgUrl, MemberAuthPhotoType type) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        MemberAuthPhoto memberAuthPhoto = MemberAuthPhoto.builder()
                .member(member)
                .imageUrl(imgUrl)
                .authType(type)
                .authStatus(MemberAuthPhotoStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();
        return memberAuthPhotoRepository.save(memberAuthPhoto);
    }

    public List<MemberAuthPhoto> getUnauthenticatedSignupRequestPhotoList() {
        return memberAuthPhotoRepository.findAllByMemberStatusAndAuthType(MemberStatus.WAITING, MemberAuthPhotoType.SIGNUP);
    }

    public List<MemberAuthPhoto> getAuthenticatedFreshmanCouponRequestPhotoList(Long memberId) {
        return memberAuthPhotoRepository.findAllByAuthStatusNotAndAuthTypeAndMemberId(MemberAuthPhotoStatus.REJECTED, MemberAuthPhotoType.FRESHMAN_COUPON, memberId);

    }

    public List<MemberAuthPhoto> getUnauthenticatedFreshmanCouponRequestPhotoList() {
        return memberAuthPhotoRepository.findAllByAuthStatusAndAuthType(MemberAuthPhotoStatus.WAITING, MemberAuthPhotoType.FRESHMAN_COUPON);
    }

    public MemberAuthPhoto getById(Long memberId, MemberAuthPhotoType type) {
        return memberAuthPhotoRepository.findByMemberIdAndAuthType(memberId, type)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
    
}
