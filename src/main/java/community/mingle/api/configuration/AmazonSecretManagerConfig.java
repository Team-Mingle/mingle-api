package community.mingle.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import static community.mingle.api.configuration.ProjectBaseConfiguration.Profile.DEV;
import static community.mingle.api.configuration.ProjectBaseConfiguration.Profile.LOCAL;
import static software.amazon.awssdk.regions.Region.AP_NORTHEAST_2;


@Configuration
public class AmazonSecretManagerConfig {

    public static final String SECRET_MANAGER_CLIENT = "secretManagerClient";

    @Bean(SECRET_MANAGER_CLIENT)
    @Profile(DEV)
    public SecretsManagerClient secretsManagerDevClient() {
        EnvironmentVariableCredentialsProvider environmentVariableCredentialsProvider = EnvironmentVariableCredentialsProvider.create();
        return SecretsManagerClient.builder()
                .credentialsProvider(environmentVariableCredentialsProvider)
                .region(AP_NORTHEAST_2)
                .build();
    }

    @Bean(SECRET_MANAGER_CLIENT)
    @Profile(LOCAL)
    public SecretsManagerClient secretsManagerLocalClient() {
        return SecretsManagerClient.builder()
                .region(AP_NORTHEAST_2)
                .build();
    }
}
