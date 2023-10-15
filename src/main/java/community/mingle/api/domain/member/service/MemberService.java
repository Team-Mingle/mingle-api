package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.entity.AuthenticationCode;
import community.mingle.api.domain.member.repository.AuthRepository;
import community.mingle.api.domain.member.repository.AuthenticationCodeRepository;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthRepository authRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;


    public void verifyEmail (String email) {
        if (authRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (authRepository.findByEmail(email).getStatus().equals(MemberStatus.INACTIVE)) {
            throw new CustomException(ErrorCode.MEMBER_DELETED);
        }
    }


    public void registerAuthEmail(String email, String code) {

        AuthenticationCode authenticationCode = AuthenticationCode.builder()
                                                                    .email(email)
                                                                    .authToken(code)
                                                                    .build();
        authenticationCodeRepository.save(authenticationCode);

    }


    public String verifyCode(String email, String code) {

        String domain = email.split("@")[1];
        LocalDateTime now = LocalDateTime.now();

        AuthenticationCode authenticationCode = getAuthenticationCode(email);
        checkCodeMatch(code, authenticationCode.getAuthToken());

        if (domain.equals("freshman.mingle.com")) {
            return "새내기 인증이 완료되었습니다.";
        }

        checkCodeValidity(authenticationCode, now);
        return "인증이 완료되었습니다.";
    }

    private AuthenticationCode getAuthenticationCode(String email) {
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByEmail(email);
        if (authenticationCode == null) {
            throw new CustomException(ErrorCode.CODE_FOUND_FAILED);
        }
        return authenticationCode;
    }

    private void checkCodeMatch(String inputCode, String storedCode) {
        if (!inputCode.equals(storedCode)) {
            throw new CustomException(ErrorCode.CODE_MATCH_FAILED);
        }
    }

    private void checkCodeValidity(AuthenticationCode authenticationCode, LocalDateTime now) {
        if (now.isAfter(authenticationCode.getCreatedAt().plusMinutes(3))) {
            throw new CustomException(ErrorCode.CODE_VALIDITY_EXPIRED);
        }
    }

}
