package community.mingle.api.global.amplitude;

import java.util.List;

public record AmplitudeRequest(
        String api_key,
        List<AmplitudeEvent> events
        ) {
}
