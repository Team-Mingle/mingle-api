package community.mingle.api.domain.auth.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
@Builder
public class LoginMemberResponse {
    private Long memberId;
    private String hashedEmail;
    private String nickName;
    private String univName;
    private String country;
    private String accessToken;
    private String refreshToken;
}

