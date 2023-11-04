package community.mingle.api.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ProjectBaseConfiguration {

    private final Environment environment;

    @Bean
    public String basePackage() {
        return BASE_PACKAGE;
    }

    @Bean
    public String projectName() {
        return PROJECT_NAME;
    }

    @Bean
    public String profile() {
        List<String> excludedProfiles = Arrays.asList(
                Profile.LOCAL,
                Profile.DEV,
                Profile.PROD
        );

        Optional<String> activeProfile = Arrays.stream(environment.getActiveProfiles())
                .filter(excludedProfiles::contains)
                .findFirst();

        return activeProfile.orElse("");
    }


    public static final String BASE_PACKAGE = "community.mingle.api";
    public static final String PROJECT_NAME = "mingle-api";

    public static class Profile {
        public static final String LOCAL = "local";
        public static final String DEV = "dev";
        public static final String PROD = "prod";
    }

}
