package community.mingle.api.configuration;

import com.amplitude.Amplitude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmplitudeConfiguration {

    @Bean
    public Amplitude amplitude() {
        Amplitude amplitude = Amplitude.getInstance();
        amplitude.init("86848239b2e18a1d839b522148b406e1");
        return amplitude;
    }
}
