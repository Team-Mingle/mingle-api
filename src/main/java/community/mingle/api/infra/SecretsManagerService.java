package community.mingle.api.infra;

import community.mingle.api.dto.security.DevTokenDto;
import org.springframework.stereotype.Service;

@Service
public class SecretsManagerService {

    public String getJwtSecretKey() {
        //TODO aws secrets manager 설정하기
        return "jwt-secret-key";
    }

    public DevTokenDto getJwtDevToken() {
        return new DevTokenDto("mingleUser", "mingleAdmin", "mingleKsa");
    }
}
