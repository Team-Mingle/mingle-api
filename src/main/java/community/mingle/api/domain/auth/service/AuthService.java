package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.auth.entity.AuthenticationCode;
import community.mingle.api.domain.auth.repository.AuthenticationCodeRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.global.utils.EmailHasher;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public static final String VERIFICATION_CODE_EMAIL_SUBJECT = "Mingle의 이메일 인증번호를 확인하세요";
    public static final String FRESHMAN_EMAIL_DOMAIN = "freshman.mingle.com";


    public String createCode() {

        Random random = new Random();
        return String.valueOf(random.nextInt(888888) + 111111);

    }

    public void sendAuthEmail(String emailTo, String authKey) {

        Context context = new Context();
        context.setVariable("authKey", authKey);

        String html = springTemplateEngine.process("index", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            messageHelper.setFrom("admin@mingle.community");
            messageHelper.setTo(emailTo);
            messageHelper.setSubject(VERIFICATION_CODE_EMAIL_SUBJECT);
            messageHelper.setText(html, true);
            messageHelper.addInline("image", new ClassPathResource("templates/images/image-1.jpeg"));

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }



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

    public Boolean verifyCode(String email, String code) {

        String domain = extractDomain(email);
        LocalDateTime now = LocalDateTime.now();

        AuthenticationCode authenticationCode = getAuthenticationCode(email);
        checkCodeMatch(code, authenticationCode.getAuthToken());

        if (domain.equals(FRESHMAN_EMAIL_DOMAIN)) {
            return true;
        }

        checkCodeValidity(authenticationCode, now);
        return true;
    }

    public String extractDomain(String email) {
        return email.split("@")[1];
    }

    private AuthenticationCode getAuthenticationCode(String email) {
        String hashedEmail = EmailHasher.hashEmail(email);
        return authenticationCodeRepository.findByEmail(hashedEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.CODE_FOUND_FAILED));
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
