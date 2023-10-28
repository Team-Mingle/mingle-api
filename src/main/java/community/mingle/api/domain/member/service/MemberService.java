package community.mingle.api.domain.member.service;

import community.mingle.api.domain.auth.entity.AuthenticationCode;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.auth.repository.AuthenticationCodeRepository;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.global.utils.EmailHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import community.mingle.api.domain.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationCodeRepository authenticationCodeRepository;


    public Member getMemberByHashedEmail(String hashedEmail) {
        return memberRepository.findByEmail(hashedEmail)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public Member getMemberByEmail(String email) {
        String hashedEmail = EmailHasher.hashEmail(email);
        return getMemberByHashedEmail(hashedEmail);
    }

    public void updateFcmToken(Member member, String fcmToken) {
        member.setFcmToken(fcmToken);
    }


    public void updatePwd(Member member, String pwd) {
        String encodedPwd = passwordEncoder.encode(pwd);
        member.updatePwd(encodedPwd);
    }
}
