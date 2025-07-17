package com.promotion.engine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test for the main application class.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb",
    "spring.r2dbc.username=sa",
    "spring.r2dbc.password=",
    "spring.r2dbc.primary.host=localhost",
    "spring.r2dbc.primary.port=5432",
    "spring.r2dbc.primary.database=test",
    "spring.r2dbc.primary.username=test",
    "spring.r2dbc.primary.password=test",
    "spring.r2dbc.fallback.host=localhost",
    "spring.r2dbc.fallback.port=5432",
    "spring.r2dbc.fallback.database=fallback",
    "spring.r2dbc.fallback.username=test",
    "spring.r2dbc.fallback.password=test"
})
class PromotionEngineApplicationTest {

    @Test
    void testApplicationContextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
        assertTrue(true, "Application context should load successfully");
    }

    @Test
    void testMainMethodExists() {
        // Test that the main method exists and can be accessed
        assertDoesNotThrow(() -> {
            PromotionEngineApplication.class.getMethod("main", String[].class);
        });
    }
} 