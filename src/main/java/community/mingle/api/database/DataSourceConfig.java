package community.mingle.api.database;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceConfig {
    String username;
    String password;
    String engine;
    String host;
    String port;
    String dbname;
}
