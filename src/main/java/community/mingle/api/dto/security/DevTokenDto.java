package community.mingle.api.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DevTokenDto {
    private String mingleUser;
    private String mingleAdmin;
    private String mingleKsa;
}
