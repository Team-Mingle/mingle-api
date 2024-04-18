package community.mingle.api.configuration;

import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmplitudeConfiguration {

    private final SecretsManagerService secretsManagerService;

    @Bean
    public String amplitudeApiKey() {
        return secretsManagerService.getAmplitudeApiKey();
    }
}
