package community.mingle.api.domain.auth.service;

import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

//    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

//    @Value("${spring.mail.username}")
    private String emailFrom;

    public String createCode() {

        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);
        return authKey;

    }


    public void sendAuthEmail(String emailTo, String authKey) {

        String subject = "Mingle의 이메일 인증번호를 확인하세요";

        Context context = new Context();
        context.setVariable("authKey", authKey);

        String html = springTemplateEngine.process("index", context);

//        try {
//
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
//
//            helper.setFrom(emailFrom);
//            helper.setTo(emailTo);
//            helper.setSubject(subject);
//            helper.setText(html, true);
//            helper.addInline("image", new ClassPathResource("templates/images/image-1.jpeg"));
//
//            javaMailSender.send(mimeMessage);
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
//        }


    }
}
