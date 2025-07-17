package com.promotion.engine.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

/**
 * Test database configuration that mocks the fallback database template
 * to avoid PostgreSQL connection issues in tests.
 */
@TestConfiguration
public class TestDatabaseConfig {

    /**
     * Mock fallback R2DBC template for tests.
     * This prevents the need for actual fallback database connections.
     *
     * @return A mocked R2dbcEntityTemplate
     */
    @Bean("fallbackR2dbcTemplate")
    @Primary
    public R2dbcEntityTemplate fallbackR2dbcTemplate() {
        R2dbcEntityTemplate mockTemplate = mock(R2dbcEntityTemplate.class);
        
        // Mock the select method to return an empty result
        when(mockTemplate.select(any(Class.class)))
            .thenReturn(mock(org.springframework.data.r2dbc.core.ReactiveSelectOperation.ReactiveSelect.class));
        
        return mockTemplate;
    }
} 