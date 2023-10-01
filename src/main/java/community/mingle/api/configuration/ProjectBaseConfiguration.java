package community.mingle.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBaseConfiguration {

    @Bean
    public String basePackage() {
        return BASE_PACKAGE;
    }

    @Bean
    public String projectName() {
        return PROJECT_NAME;
    }

    public static final String BASE_PACKAGE = "community.mingle.api";
    public static final String PROJECT_NAME = "mingle-api";
}
