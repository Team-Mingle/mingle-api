package community.mingle.api.domain.auth.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class CountryResponse {
    private String country;

    public CountryResponse(String country) {
        this.country = country;
    }

}

