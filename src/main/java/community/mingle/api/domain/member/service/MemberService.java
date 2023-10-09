package community.mingle.api.domain.member.service;

import community.mingle.api.domain.authentication.entity.AuthenticationCode;
import community.mingle.api.domain.member.repository.AuthRepository;
import community.mingle.api.domain.member.repository.AuthenticationCodeRepository;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.domain.member.controller.request.PostCodeRequest;
import community.mingle.api.domain.member.controller.request.PostEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthRepository authRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;

    @Transactional
    public void verifyEmail (String email) {
        if (authRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (authRepository.findByEmail(email).getStatus().equals(MemberStatus.INACTIVE)) {
            throw new CustomException(ErrorCode.MEMBER_DELETED);
        }
    }

    @Transactional
    public void registerAuthEmail(String email, String code) {

        AuthenticationCode authenticationCode = AuthenticationCode.builder().email(email).authToken(code).build();
        authenticationCodeRepository.save(authenticationCode);

    }

    @Transactional
    public String verifyCode(String email, String code) {

        String domain = email.split("@")[1];
        LocalDateTime now = LocalDateTime.now();

        AuthenticationCode authenticationCode = authenticationCodeRepository.findByEmail(email);

        if (authenticationCode == null) {
            throw new CustomException(ErrorCode.CODE_FOUND_FAILED);
        }

        if (domain.equals("freshman.mingle.com")) {
            if (!(code.equals(authenticationCode.getAuthToken()))){
                throw new CustomException(ErrorCode.CODE_MATCH_FAILED);
            }
            return "새내기 인증이 완료되었습니다.";
        }

        if (now.isAfter(authenticationCode.getCreatedAt().plusMinutes(3))) {
            throw new CustomException(ErrorCode.CODE_VALIDITY_EXPIRED);
        }
        if (!(code.equals(authenticationCode.getAuthToken()))){
            throw new CustomException(ErrorCode.CODE_MATCH_FAILED);
        }

        return "인증이 완료되었습니다.";
    }

}
