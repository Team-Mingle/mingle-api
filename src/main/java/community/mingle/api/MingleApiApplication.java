package community.mingle.api;

import community.mingle.api.configuration.ProjectBaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class MingleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingleApiApplication.class, args);
    }

}
