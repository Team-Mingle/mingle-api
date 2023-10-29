package community.mingle.api.dto.security;

import lombok.Data;

@Data
public class CreatedTokenDto {
    private final String accessToken;
    private final String refreshToken;
}
