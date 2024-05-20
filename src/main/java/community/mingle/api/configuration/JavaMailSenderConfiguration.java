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

        javaMailSender.setHost("smtp-relay.gmail.com");
        javaMailSender.setPort(25);
        javaMailSender.setUsername("no-reply@mingle.community");
        String password = secretsManagerService.getMailSenderPassword();
        javaMailSender.setPassword(password);
        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }
}
