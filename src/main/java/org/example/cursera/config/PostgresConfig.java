package org.example.cursera.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager",
        basePackages = {"org.example.cursera.domain.repository"}
)
public class PostgresConfig {

    @Primary
    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "postgresEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("postgresDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(dataSource)
                .packages("org.example.cursera.domain.entity")
                .persistenceUnit("postgres")
                .build();

        em.setJpaPropertyMap(new HashMap<String, Object>() {{
            put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            put("hibernate.show_sql", false);
            put("hibernate.format_sql", true);
        }});

        return em;
    }

    @Primary
    @Bean(name = "postgresTransactionManager")
    public PlatformTransactionManager productTransactionManager(
            @Qualifier("postgresEntityManagerFactory") EntityManagerFactory postgresEntityManagerFactory
    ) {
        return new JpaTransactionManager(postgresEntityManagerFactory);
    }


}
