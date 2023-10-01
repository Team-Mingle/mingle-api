package community.mingle.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceUtil {

    public HikariDataSource createHikariDataSource(
            String poolName,
            DataSourceConfig dataSourceConfig
    ) {
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s",
                dataSourceConfig.host,
                dataSourceConfig.port,
                dataSourceConfig.dbname);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(poolName);
        hikariConfig.setDriverClassName(MYSQL);
        hikariConfig.setUsername(dataSourceConfig.username);
        hikariConfig.setPassword(dataSourceConfig.password);
        hikariConfig.setJdbcUrl(jdbcUrl);

        return new HikariDataSource(hikariConfig);
    }

    public static final String MYSQL = "com.mysql.cj.jdbc.Driver";
}
