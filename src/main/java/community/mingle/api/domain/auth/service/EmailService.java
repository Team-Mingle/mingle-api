package community.mingle.api.domain.auth.service;

import com.fasterxml.jackson.databind.ext.Java7HandlersImpl;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public static final String VERIFICATION_CODE_EMAIL_SUBJECT = "Mingle의 이메일 인증번호를 확인하세요";

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


}
