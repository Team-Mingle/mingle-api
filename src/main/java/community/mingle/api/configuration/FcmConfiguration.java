package community.mingle.api.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
class FcmConfiguration {

    private final SecretsManagerService secretsManagerService;
    private final String profile;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream firebaseToken = convertToStream(secretsManagerService.getFcmToken(profile));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseToken))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    private InputStream convertToStream(String token) {
        return new ByteArrayInputStream(token.getBytes());
    }

}