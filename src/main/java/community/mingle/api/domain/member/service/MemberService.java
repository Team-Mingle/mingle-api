package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * encrypted 된 이메일 넘기기
     * @param  email    String
     */
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public void checkPassword(String rawPassword, String storedPasswordHash) {
        if (!passwordEncoder.matches(rawPassword, storedPasswordHash))
            throw new CustomException(FAILED_TO_LOGIN);
    }

    public Member isValidEmailAndPwd(String rawEmail, String reqPwd) throws CustomException {
        try {
            String hashedEmail = EmailHasher.hashEmail(rawEmail);
            Member member = getMemberByEmail(hashedEmail);
            checkPassword(reqPwd, member.getPassword());
            return member;
        } catch (CustomException e) {
            throw new CustomException(FAILED_TO_LOGIN); //Failed to login exception으로 통일하기 위해 사용
        }
    }


    /**
     * 탈퇴, 신고된 유저 재로그인 방지
     * @param member    Member
     */
    public void validateLoginMemberStatusIsActive(Member member) {
        //탈퇴 유저 로그인 방지
        if (member.getStatus().equals(MemberStatus.INACTIVE)) {
            throw new CustomException(MEMBER_DELETED_ERROR);
        }
        //신고 유저 로그인 방지
        if (member.getStatus().equals(MemberStatus.REPORTED)) {
            throw new CustomException(MEMBER_REPORTED_ERROR);
        }
    }

    public void updateFcmToken(Member member, String fcmToken) {
        member.setFcmToken(fcmToken);
    }


    public void updatePwd(Member member, String pwd) {
        String encodedPwd = passwordEncoder.encode(pwd);
        member.updatePwd(encodedPwd);
    }
}
