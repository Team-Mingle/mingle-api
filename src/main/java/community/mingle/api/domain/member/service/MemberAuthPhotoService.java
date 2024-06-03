package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.MemberAuthPhoto;
import community.mingle.api.domain.member.repository.MemberAuthPhotoRepository;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberAuthPhotoService {

    private final MemberAuthPhotoRepository memberAuthPhotoRepository;
    private final MemberRepository memberRepository;

    public MemberAuthPhoto create(Long memberId, String imgUrl) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        MemberAuthPhoto memberAuthPhoto = MemberAuthPhoto.builder()
                .member(member)
                .imageUrl(imgUrl)
                .build();
        return memberAuthPhotoRepository.save(memberAuthPhoto);
    }

    public List<MemberAuthPhoto> getUnauthenticatedPhotoList() {
        return memberAuthPhotoRepository.findAllByMemberStatus(MemberStatus.WAITING);
    }
    
}
