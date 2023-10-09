package community.mingle.api.dto.security;

import community.mingle.api.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private Long memberId;
    private MemberRole memberRole;
}
