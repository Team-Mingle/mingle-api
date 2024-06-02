package community.mingle.api.domain.member.service;

import community.mingle.api.domain.auth.repository.RefreshTokenRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.member.repository.UniversityRepository;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.utils.AuthHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Member getByHashedEmail(String hashedEmail) {
        return memberRepository.findByEmail(hashedEmail)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public Member getByEmail(String email) {
        String hashedEmail = AuthHasher.hashString(email);
        return getByHashedEmail(hashedEmail);
    }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public Boolean existsByEmail(String email) {
        String hashedEmail = AuthHasher.hashString(email);
        return memberRepository.existsByEmail(hashedEmail);
    }

    public Boolean existsByStudentId(String studentId, int universityId) {
        return memberRepository.existsByStudentIdAndUniversityId(studentId, universityId);
    }

    public Boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public Member create(int universityId, String nickname, String email, String password) {
        String hashedEmail = AuthHasher.hashString(email);
        String encodedPassword = AuthHasher.hashString(password);

        University university = universityRepository.findById(universityId).orElseThrow(() -> new CustomException(UNIVERSITY_NOT_FOUND));

        Member member = Member.builder()
                .university(university)
                .nickname(nickname)
                .email(hashedEmail)
                .password(encodedPassword)
                .agreedAt(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .role(MemberRole.USER)
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public Member tempCreate(int universityId, String nickname, String email, String password, String fcmToken, String studentId) {
        String hashedEmail = AuthHasher.hashString(email);
        String encodedPassword = AuthHasher.hashString(password);

        University university = universityRepository.findById(universityId).orElseThrow(() -> new CustomException(UNIVERSITY_NOT_FOUND));

        Member member = Member.builder()
                .university(university)
                .nickname(nickname)
                .email(hashedEmail)
                .password(encodedPassword)
                .agreedAt(LocalDateTime.now())
                .status(MemberStatus.WAITING)
                .role(MemberRole.USER)
                .fcmToken(fcmToken)
                .studentId(studentId)
                .rawEmail(email)
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public void setFcmToken(Member member, String fcmToken) {
        member.setFcmToken(fcmToken);
    }


    @Transactional
    public void updatePassword(Member member, String password) {
        String encodedPassword = AuthHasher.hashString(password);
        member.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteRefreshToken(Member member) {
        refreshTokenRepository.findById(member.getEmail())
                .ifPresentOrElse(refreshTokenRepository::delete, () -> {
                });
    }

    public void checkIsSameMemberAsTokenMember(Member tokenMember, Member member) {
        if (!tokenMember.getEmail().equals(member.getEmail())) {
            throw new CustomException(FAILED_TO_LOGIN);
        }
    }

    @Transactional
    public void updateEmail(Member member, String email) {
        String encodedEmail = AuthHasher.hashString(email);
        member.updateEmail(encodedEmail);
    }


}
