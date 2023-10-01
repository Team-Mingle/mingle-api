package community.mingle.api.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DataSourceConfig {
    String username;
    String password;
    String engine;
    String host;
    String port;
    String dbname;
}
