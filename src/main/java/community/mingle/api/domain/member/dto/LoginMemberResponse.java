package community.mingle.api.domain.member.dto;

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
    private String email;
    private String nickName;
    private String univName;
    private String accessToken;
    private String refreshToken;
}

