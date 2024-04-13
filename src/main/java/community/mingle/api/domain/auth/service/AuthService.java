package community.mingle.api.domain.auth.service;

import community.mingle.api.domain.auth.entity.AuthenticationCode;
import community.mingle.api.domain.auth.entity.Policy;
import community.mingle.api.domain.auth.repository.AuthenticationCodeRepository;
import community.mingle.api.domain.auth.repository.PolicyRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.notification.event.TempSignUpNotificationEvent;
import community.mingle.api.enums.MemberStatus;
import community.mingle.api.enums.PolicyType;
import community.mingle.api.enums.TempSignUpStatusType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.utils.AuthHasher;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;


    public static final String VERIFICATION_CODE_EMAIL_SUBJECT = "Mingle의 이메일 인증번호를 확인하세요";
    public static final String TEMP_SIGNUP_PROCESSING_EMAIL_SUBJECT = "Mingle 회원가입 인증 요청이 완료되었습니다";
    public static final String TEMP_SIGNUP_APPROVED_EMAIL_SUBJECT = "Mingle 회원가입이 완료되었습니다";
    public static final String TEMP_SIGNUP_REJECTED_EMAIL_SUBJECT = "Mingle 회원가입을 다시 한 번 확인해주세요.";
    public static final String TEMP_SIGNUP_NOTIFICATION_CONTENT = "자세한 내용은 이메일을 확인해 주세요!";
    public static final String TEMP_SIGNUP_TO_ADMIN_EMAIL_SUBJECT = "임시 회원가입 요청! 백오피스를 확인해주세요.";

    public static final String FRESHMAN_EMAIL_DOMAIN = "freshman.mingle.com";
    private final PolicyRepository policyRepository;


    public void verifyEmail(String email) {

        String hashedEmail = AuthHasher.hashString(email);

        Optional<Member> member = memberRepository.findByEmail(hashedEmail);
        if (member.isPresent()) {
            throw new CustomException(EMAIL_DUPLICATED);
        }
    }

    public String createCode() {

        Random random = new Random();
        return String.valueOf(random.nextInt(888888) + 111111);

    }

    @Transactional
    public void registerAuthEmail(String email, String code) {
        String hashedEmail = AuthHasher.hashString(email);
        AuthenticationCode authenticationCode = AuthenticationCode.builder()
                .email(hashedEmail)
                .authToken(code)
                .build();
        authenticationCodeRepository.save(authenticationCode);
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
            throw new CustomException(EMAIL_SEND_FAILED);
        }
    }

    @Async
    //Transactional 안됨
    public void sendTempSignUpEmail(String emailTo, TempSignUpStatusType emailType) {
        Context context = new Context();
        String html = null;
        String subject = null;
        switch (emailType) {
            case PROCESSING :
                subject = TEMP_SIGNUP_PROCESSING_EMAIL_SUBJECT;
                html = springTemplateEngine.process("tempSignUp", context);
                break;
            case APPROVED:
                subject = TEMP_SIGNUP_APPROVED_EMAIL_SUBJECT;
                html = springTemplateEngine.process("tempSignUpApproved", context);
                break;
            case REJECTED:
                subject = TEMP_SIGNUP_REJECTED_EMAIL_SUBJECT;
                html = springTemplateEngine.process("tempSignUpRejected", context);
                break;
            case ADMIN:
                subject = TEMP_SIGNUP_TO_ADMIN_EMAIL_SUBJECT;
                html = springTemplateEngine.process("tempSignUpToAdmin", context);
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            messageHelper.setFrom("admin@mingle.community");
            messageHelper.setTo(emailTo);
            messageHelper.setSubject(subject);
            messageHelper.setText(html, true);
            messageHelper.addInline("image", new ClassPathResource("templates/images/image-1.jpeg"));

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new CustomException(EMAIL_SEND_FAILED);
        }
    }




    public Boolean verifyCode(String email, String code) {

        String domain = extractDomain(email);
        LocalDateTime now = LocalDateTime.now();

        AuthenticationCode authenticationCode = getAuthenticationCodeWithPureEmail(email);
        checkCodeMatch(code, authenticationCode.getAuthToken());

        if (domain.equals(FRESHMAN_EMAIL_DOMAIN)) {
            return true;
        }

        checkCodeValidity(authenticationCode, now);
        return true;
    }

    public void verifyCodeForChangePassword(String email, String code) {
        AuthenticationCode authenticationCode = getAuthenticationCodeByHashedEmail(email);
        checkCodeMatch(code, authenticationCode.getAuthToken());
    }

    public String extractDomain(String email) {
        return email.split("@")[1];
    }

    public void checkPassword(String rawPassword, String storedPasswordHash) {
        if (!AuthHasher.hashString(rawPassword).equals(storedPasswordHash))
            throw new CustomException(FAILED_TO_LOGIN);
    }

    public void checkMemberStatusActive(Member member) {
        if (member.getStatus().equals(MemberStatus.INACTIVE)) {
            throw new CustomException(MEMBER_DELETED_ERROR);
        }
        if (member.getStatus().equals(MemberStatus.WAITING) || member.getStatus().equals(MemberStatus.REJECTED)) {
            throw new CustomException(MEMBER_UNAUTHENTICATED_ERROR);
        }
        if (member.getStatus().equals(MemberStatus.REPORTED)) {
            throw new CustomException(MEMBER_REPORTED_ERROR);
        }
    }

    public Policy getPolicy(PolicyType policyType) {
        return policyRepository.findById(policyType.getDbPolicyName()).orElseThrow(() -> new CustomException(POLICY_NOT_FOUND));
    }


    private AuthenticationCode getAuthenticationCodeWithPureEmail(String email) {
        String hashedEmail = AuthHasher.hashString(email);
        return authenticationCodeRepository.findFirstByEmailOrderByCreatedAtDesc(hashedEmail)
                .orElseThrow(() -> new CustomException(CODE_FOUND_FAILED));
    }

    private AuthenticationCode getAuthenticationCodeByHashedEmail(String email) {
        return authenticationCodeRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new CustomException(CODE_FOUND_FAILED));
    }

    private void checkCodeMatch(String inputCode, String storedCode) {
        if (!inputCode.equals(storedCode)) {
            throw new CustomException(CODE_MATCH_FAILED);
        }
    }

    private void checkCodeValidity(AuthenticationCode authenticationCode, LocalDateTime now) {
        if (now.isAfter(authenticationCode.getCreatedAt().plusMinutes(3))) {
            throw new CustomException(CODE_VALIDITY_EXPIRED);
        }
    }

    public void sendTempSignUpNotification(String fcmToken, TempSignUpStatusType tempSignUpStatusType) {
        String title = switch (tempSignUpStatusType) {
            case PROCESSING -> TEMP_SIGNUP_PROCESSING_EMAIL_SUBJECT;
            case APPROVED -> TEMP_SIGNUP_APPROVED_EMAIL_SUBJECT;
            case REJECTED -> TEMP_SIGNUP_REJECTED_EMAIL_SUBJECT;
            case ADMIN -> null;
        };
        String content = TEMP_SIGNUP_NOTIFICATION_CONTENT;
        applicationEventPublisher.publishEvent(
                new TempSignUpNotificationEvent(this, fcmToken , title, content)
        );
    }
}
