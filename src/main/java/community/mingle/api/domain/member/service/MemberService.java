package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.auth.repository.AuthenticationCodeRepository;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import community.mingle.api.domain.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public Member getMemberByHashedEmail(String hashedEmail) {
        return memberRepository.findByEmail(hashedEmail)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public Member getMemberByEmail(String email) {
        String hashedEmail = EmailHasher.hashEmail(email);
        return getMemberByHashedEmail(hashedEmail);
    }

    @Transactional
    public void setFcmToken(Member member, String fcmToken) {
        member.setFcmToken(fcmToken);
    }


    @Transactional
    public void updatePassword(Member member, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        member.updatePassword(encodedPassword);
    }
}
