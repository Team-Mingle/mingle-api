package community.mingle.api.dto.security;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevTokenDto {
    private String mingleUser;
    private String mingleAdmin;
    private String mingleKsa;
}
