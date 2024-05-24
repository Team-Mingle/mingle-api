package community.mingle.api.configuration;

import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaMailSenderConfiguration {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true); // 추가 : https://bootcamptoprod.com/spring-boot-email/
        properties.put("mail.smtp.ssl.trust", "smtp-relay.gmail.com"); // SSL 설정 추가
        properties.put("mail.debug", true); // 디버그 모드 활성화

        javaMailSender.setHost("smtp-relay.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("no-reply@mingle.community");
        javaMailSender.setPassword("wymbigouckqkpfmm");
        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }
}
