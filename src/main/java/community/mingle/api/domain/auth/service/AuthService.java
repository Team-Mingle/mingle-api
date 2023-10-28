package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.auth.entity.AuthenticationCode;
import community.mingle.api.domain.auth.repository.AuthenticationCodeRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.global.utils.EmailHasher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final MemberRepository memberRepository;

    public void verifyEmail (String email) {

        String hashedEmail = EmailHasher.hashEmail(email);

        Optional<Member> member = memberRepository.findByEmail(hashedEmail);
        if (member.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
    }

    @Transactional
    public void registerAuthEmail(String email, String code) {
        String hashedEmail = EmailHasher.hashEmail(email);
        AuthenticationCode authenticationCode = AuthenticationCode.builder()
                .email(hashedEmail)
                .authToken(code)
                .build();
        authenticationCodeRepository.save(authenticationCode);
    }
}
