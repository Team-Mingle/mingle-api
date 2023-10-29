package community.mingle.api;

import community.mingle.api.configuration.ProjectBaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MingleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingleApiApplication.class, args);
    }

}
