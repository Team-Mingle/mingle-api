package community.mingle.api.configuration;

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

        javaMailSender.setHost("smtp-relay.gmail.com");
        javaMailSender.setPort(25);
        javaMailSender.setUsername("admin@mingle.community");
        javaMailSender.setPassword("scqmxysuzzxqphdm");
        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }
}
