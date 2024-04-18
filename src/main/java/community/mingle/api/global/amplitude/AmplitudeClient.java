package community.mingle.api.global.amplitude;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "amplitudeProd", url = "https://api2.amplitude.com")
interface AmplitudeClient {

    @PostMapping("/2/httpapi")
    public void logAmplitude(
            @RequestBody
            AmplitudeRequest request
    );
}
