package community.mingle.api.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.mingle.api.database.DataSourceConfig;
import community.mingle.api.dto.security.DevTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static community.mingle.api.configuration.AmazonSecretManagerConfig.SECRET_MANAGER_CLIENT;

@Service
@RequiredArgsConstructor
public class SecretsManagerService {

    private final String projectName;
    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    private final SecretsManagerClient secretsManager;

    private Map<String, String> arns = new HashMap<>();

    private Map<String, String> getArns() {
        if (arns.isEmpty()) {

            List<SecretListEntry> secretList = secretsManager.listSecretsPaginator()
                    .stream()
                    .flatMap(resp -> resp.secretList().stream())
                    .toList();

            for (SecretListEntry secret : secretList) {
                arns.put(secret.name(), secret.arn());
            }
        }

        return arns;
    }

    public DevTokenDto getJwtDevToken() throws IOException {
        return getSecretValue("mingle-api/jwt-dev-token", DevTokenDto.class);
    }

    public String getS3BucketName() {
        return getSecretValueString("mingle-api/s3-bucket");
    }

    public String getJwtSecretKey() {
        return getSecretValueString("mingle-api/jwt-secret-key");
    }
    public String getRefreshJwtSecretKey() {
        return getSecretValueString("mingle-api/refresh-jwt-secret-key");
    }

    public DataSourceConfig getDataSourceConfig(String profile) throws IOException {
        return getSecretValue(projectName + "/" + profile + "/db", DataSourceConfig.class);
    }

    public String getFcmToken(String profile)  {
        return getSecretValueString(projectName + "/" + profile + "/fcm-token");
    }

    private String getSecretValueString(String name) {
        String arn = getArns().get(name);
        if (arn == null) {
            throw new IllegalArgumentException("Cannot find secret id: " + name);
        }

        return secretsManager.getSecretValue(builder -> builder.secretId(arn)).secretString();
    }

    private <T> T getSecretValue(String id, Class<T> valueType) throws IOException {
        String value = getSecretValueString(id);
        return objectMapper.readValue(value, valueType);
    }
}
