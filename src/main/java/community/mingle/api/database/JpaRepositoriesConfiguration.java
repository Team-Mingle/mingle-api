package community.mingle.api.database;

import community.mingle.api.configuration.ProjectBaseConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@EnableJpaRepositories(
        basePackages = ProjectBaseConfiguration.BASE_PACKAGE
)
@Configuration
@RequiredArgsConstructor
public class JpaRepositoriesConfiguration {

    private final String basePackage;
    private final String projectName;
    private final ConfigurableListableBeanFactory beanFactory;
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSourceUtil().createHikariDataSource(
                "mingle-db-connection",
                DataSourceConfig.builder()
                        .username("root")
                        .password("mingle")
                        .engine("mysql")
                        .host("localhost")
                        .port("7071")
                        .dbname("mingle")
                        .build()
        );

        return new LazyConnectionDataSourceProxy(hikariDataSource);
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HashMap<String, Object> jpaPropertyMap = new HashMap<>(){{
            put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
            put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
            put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
        }};

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(basePackage);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(jpaPropertyMap);
        em.setPersistenceUnitName(projectName);

        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        jpaTransactionManager.setPersistenceUnitName(projectName);
        return jpaTransactionManager;
    }
}
