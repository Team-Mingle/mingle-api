package community.mingle.api.global.amplitude;

import java.util.Map;

public record AmplitudeEvent(
        String user_id,
        String device_id,
        String event_type,
        Map<String, String> event_properties
) {
}
