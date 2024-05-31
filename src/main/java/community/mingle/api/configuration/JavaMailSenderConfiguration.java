package community.mingle.api.configuration;

import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class JavaMailSenderConfiguration {
    private final SecretsManagerService secretsManagerService;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true); // 추가 : https://bootcamptoprod.com/spring-boot-email/
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // SSL 설정 추가
        properties.put("mail.debug", true); // 디버그 모드 활성화

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("no-reply@mingle.community");
        String password = secretsManagerService.getMailSenderPassword();
        javaMailSender.setPassword(password);
        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }
}
