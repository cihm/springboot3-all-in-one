package com.atomicjar.todos.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    private final Environment env;

    public FlywayConfig(Environment env) {
        this.env = env;
    }
//    private final PrometheusExceptionMonitor monitor;
//
//    public FlywayConfig(Environment env, PrometheusExceptionMonitor monitor) {
//        this.env = env;
//        this.monitor = monitor;
//    }

    @Bean
    @ConditionalOnProperty(name = "lewis.datasource.enabled", havingValue = "true", matchIfMissing = true)
    @Primary
    public DataSource dataSource() {
        System.out.println("lewis dataSource initialized");

//        return DataSourceBuilder.create()
//                .url(env.getProperty("lewis.datasource.url"))
//                .username(env.getProperty("lewis.datasource.username"))
//                .password(env.getProperty("lewis.datasource.password"))
//                .build();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("lewis.datasource.url"));
        config.setUsername(env.getProperty("lewis.datasource.username"));
        config.setPassword(env.getProperty("lewis.datasource.password"));
        config.setConnectionTimeout(30000);
        config.setValidationTimeout(5000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTestQuery("SELECT 1");
        return new HikariDataSource(config);
    }

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource) {

        // monitor.incrementExceptionCount();

        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
