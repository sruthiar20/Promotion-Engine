package com.promotion.engine.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

/**
 * Database configuration for the application.
 * Sets up R2DBC repositories and connection factories for both primary and fallback databases.
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "com.promotion.engine.repository")
@EnableR2dbcAuditing
public class DatabaseConfig {

    @Value("${spring.r2dbc.primary.host:localhost}")
    private String primaryHost;
    
    @Value("${spring.r2dbc.primary.port:5432}")
    private int primaryPort;
    
    @Value("${spring.r2dbc.primary.database:promotion_engine_v1}")
    private String primaryDatabase;
    
    @Value("${spring.r2dbc.primary.username:postgres}")
    private String primaryUsername;
    
    @Value("${spring.r2dbc.primary.password:postgres}")
    private String primaryPassword;
    
    @Value("${spring.r2dbc.fallback.host:localhost}")
    private String fallbackHost;
    
    @Value("${spring.r2dbc.fallback.port:5432}")
    private int fallbackPort;
    
    @Value("${spring.r2dbc.fallback.database:promotiondb}")
    private String fallbackDatabase;
    
    @Value("${spring.r2dbc.fallback.username:postgres}")
    private String fallbackUsername;
    
    @Value("${spring.r2dbc.fallback.password:postgres}")
    private String fallbackPassword;

    /**
     * Primary connection factory for promotion_engine_v1 database.
     *
     * @return The primary connection factory
     */
    @Bean("primaryConnectionFactory")
    @Primary
    public ConnectionFactory primaryConnectionFactory() {
        return new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(primaryHost)
                .port(primaryPort)
                .database(primaryDatabase)
                .username(primaryUsername)
                .password(primaryPassword)
                .build()
        );
    }
    
    /**
     * Fallback connection factory for promotiondb database.
     *
     * @return The fallback connection factory
     */
    @Bean("fallbackConnectionFactory")
    public ConnectionFactory fallbackConnectionFactory() {
        return new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(fallbackHost)
                .port(fallbackPort)
                .database(fallbackDatabase)
                .username(fallbackUsername)
                .password(fallbackPassword)
                .build()
        );
    }

    /**
     * Primary R2DBC entity template for Spring Data repositories.
     * This is required by Spring Data R2DBC repositories.
     *
     * @param connectionFactory The primary connection factory
     * @return The primary R2DBC entity template
     */
    @Bean("r2dbcEntityTemplate")
    @Primary
    public R2dbcEntityTemplate r2dbcEntityTemplate(@Qualifier("primaryConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
    
    /**
     * Creates an R2DBC entity template for the fallback database.
     *
     * @param connectionFactory The fallback connection factory
     * @return R2DBC entity template for fallback operations
     */
    @Bean("fallbackR2dbcTemplate")
    public R2dbcEntityTemplate fallbackR2dbcTemplate(@Qualifier("fallbackConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    /**
     * Initializes the primary database connection.
     *
     * @param connectionFactory The primary R2DBC connection factory
     * @return A ConnectionFactoryInitializer
     */
    @Bean("primaryInitializer")
    public ConnectionFactoryInitializer primaryInitializer(@Qualifier("primaryConnectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        return initializer;
    }
    
    /**
     * Initializes the fallback database connection.
     *
     * @param connectionFactory The fallback R2DBC connection factory
     * @return A ConnectionFactoryInitializer
     */
    @Bean("fallbackInitializer")
    public ConnectionFactoryInitializer fallbackInitializer(@Qualifier("fallbackConnectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        return initializer;
    }
} 