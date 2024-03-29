package community.mingle.api.infra;

import com.amplitude.Amplitude;
import com.amplitude.Event;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmplitudeService {

    private final Amplitude amplitude;

//    @Async
    public void logEventAsync(String eventType, Long memberId) {
        Event event = new Event(eventType, String.valueOf(memberId));
        JSONObject eventProps = new JSONObject();
        try {
            eventProps.put("Hover Time", 10).put("prop_2", "value_2");
        } catch (JSONException e) {
            System.err.println("Invalid JSON");
            e.printStackTrace();
        }
        event.eventProperties = eventProps;
        amplitude.logEvent(event);
    }

}
