package community.mingle.api.global.amplitude;

import community.mingle.api.configuration.ProjectBaseConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AmplitudeService {
    private final AmplitudeClient amplitudeClient;
    private final String profile;
    private final String amplitudeApiKey;

    @Async
    public void log(Long memberId, String eventName, Map<String, String> eventProperties) {

        if (!profile.equals(ProjectBaseConfiguration.Profile.PROD)) {
            return;
        }

        StringBuilder memberIdString = new StringBuilder(memberId.toString());
        while (memberIdString.length() < 5) {
            memberIdString.insert(0, "0");
        }
        AmplitudeEvent event = new AmplitudeEvent(
                memberIdString.toString(),
                "unknown",
                eventName,
                eventProperties
        );

        AmplitudeRequest request = new AmplitudeRequest(
                amplitudeApiKey,
                List.of(event)
        );

        amplitudeClient.logAmplitude(request);
    }
}
