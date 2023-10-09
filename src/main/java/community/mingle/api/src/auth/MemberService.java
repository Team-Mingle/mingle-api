package community.mingle.api.src.auth;

import community.mingle.api.domain.authentication.entity.AuthenticationCode;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.src.auth.model.PostCodeRequest;
import community.mingle.api.src.auth.model.PostEmailRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MemberService {

    private AuthRepository authRepository;
    private AuthCodeRepository authCodeRepository;

    @Transactional
    public void verifyEmail (PostEmailRequest postEmailRequest) {
        if (authRepository.existsByEmail(postEmailRequest.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (authRepository.findByEmail(postEmailRequest.getEmail()).getStatus().equals(MemberStatus.INACTIVE)) {
            throw new CustomException(ErrorCode.MEMBER_DELETED);
        }
    }

    @Transactional
    public void registerAuthEmail(String email, String code) {

        try {
            AuthenticationCode authenticationCode = AuthenticationCode.builder().email(email).authToken(code).build();
            authCodeRepository.save(authenticationCode);

        } catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.DATABASE_ERROR);
        }

    }

    @Transactional
    public String verifyCode(PostCodeRequest postCodeRequest) {
        String email = postCodeRequest.getEmail();
        String code = postCodeRequest.getCode();
        String domain = email.split("@")[1];
        LocalDateTime now = LocalDateTime.now();

        AuthenticationCode authenticationCode = authCodeRepository.findByEmail(email);

        if (authenticationCode == null) {
            throw new CustomException(ErrorCode.CODE_FOUND_FAILED);
        }

        if (domain.equals("freshman.mingle.com")) {
            //제한 시간 없음
            if (!(code.equals(authenticationCode.getAuthToken()))){
                throw new CustomException(ErrorCode.CODE_MATCH_FAILED);
            }
            return "새내기 인증이 완료되었습니다.";
        }

        //3분 시간제한
        if (now.isAfter(authenticationCode.getCreatedAt().plusMinutes(3))) {
            throw new CustomException(ErrorCode.CODE_VALIDITY_EXPIRED);
        }
        //해당 이메일에 해당하는 코드랑 같은지 확인
        if (!(code.equals(authenticationCode.getAuthToken()))){
            throw new CustomException(ErrorCode.CODE_MATCH_FAILED);
        }

        return "인증이 완료되었습니다.";
    }

}
