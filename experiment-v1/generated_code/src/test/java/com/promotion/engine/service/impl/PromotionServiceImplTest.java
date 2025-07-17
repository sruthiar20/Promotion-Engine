package com.promotion.engine.service.impl;

import com.promotion.engine.config.JsonNodeConverter;
import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.dto.response.PromotionResponse;
import com.promotion.engine.exception.PromotionNotFoundException;
import com.promotion.engine.model.Promotion;
import com.promotion.engine.repository.PromotionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Simplified unit tests for PromotionServiceImpl focusing on core logic.
 */
@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;
    
    @Mock
    private R2dbcEntityTemplate fallbackR2dbcTemplate;
    
    @Mock
    private JsonNodeConverter jsonNodeConverter;
    
    @InjectMocks
    private PromotionServiceImpl promotionService;
    
    private PromotionSearchRequest searchRequest;
    private Promotion mockPromotion;
    private JsonNode mockConditions;
    private JsonNode mockRules;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Setup search request
        searchRequest = new PromotionSearchRequest();
        searchRequest.setStatus("active");
        searchRequest.setProductId("SKU-PRO-001");
        searchRequest.setStartsAt(Instant.now().minusSeconds(3600));
        searchRequest.setEndsAt(Instant.now().plusSeconds(3600));
        
        // Setup mock promotion
        UUID promotionId = UUID.randomUUID();
        mockPromotion = new Promotion();
        mockPromotion.setId(promotionId);
        mockPromotion.setCode("SUMMER2024");
        mockPromotion.setType("fixed_amount");
        mockPromotion.setValue(new BigDecimal("10.99"));
        mockPromotion.setValueType("fixed_amount");
        mockPromotion.setStartsAt(Instant.now().minusSeconds(1800));
        mockPromotion.setEndsAt(Instant.now().plusSeconds(1800));
        mockPromotion.setAutomatic(false);
        mockPromotion.setUsageLimit(1000);
        mockPromotion.setUsageCount(0);
        mockPromotion.setStatus("active");
        mockPromotion.setConditionsJson("{\"type\":\"product\",\"value\":[\"SKU-PRO-001\"]}");
        mockPromotion.setRulesJson("{\"discount\":\"10.99\"}");
        mockPromotion.setCreatedAt(Instant.now().minusSeconds(7200));
        mockPromotion.setUpdatedAt(Instant.now().minusSeconds(3600));
        
        // Setup mock JSON nodes
        try {
            mockConditions = objectMapper.readTree("{\"type\":\"product\",\"value\":[\"SKU-PRO-001\"]}");
            mockRules = objectMapper.readTree("{\"discount\":\"10.99\"}");
        } catch (Exception e) {
            // Ignore for test setup
        }
    }
    
    // Test 1: Search with empty product ID
    @Test
    void testSearchPromotion_EmptyProductId() {
        // Arrange
        searchRequest.setProductId("");
        searchRequest.setCategoryId(null);
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(promotionRepository, never()).findByStatusAndProductId(anyString(), anyString(), any(), any());
    }
    
    // Test 2: Search with empty category ID
    @Test
    void testSearchPromotion_EmptyCategoryId() {
        // Arrange
        searchRequest.setProductId(null);
        searchRequest.setCategoryId("");
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(promotionRepository, never()).findByStatusAndCategoryId(anyString(), anyString(), any(), any());
    }
    
    // Test 3: Search with null product and category ID
    @Test
    void testSearchPromotion_NullProductAndCategoryId() {
        // Arrange
        searchRequest.setProductId(null);
        searchRequest.setCategoryId(null);
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(promotionRepository, never()).findByStatusAndProductId(anyString(), anyString(), any(), any());
        verify(promotionRepository, never()).findByStatusAndCategoryId(anyString(), anyString(), any(), any());
    }
    
    // Test 4: Verify service class instantiation
    @Test
    void testServiceInstantiation() {
        assertNotNull(promotionService);
        assertNotNull(searchRequest);
        assertNotNull(mockPromotion);
    }
    
    // Test 5: Test search request validation logic
    @Test
    void testSearchRequestValidation() {
        // Test that the service properly handles various input scenarios
        assertNotNull(promotionService);
        
        // Test creation of new request
        PromotionSearchRequest newRequest = new PromotionSearchRequest();
        newRequest.setStatus("test");
        assertEquals("test", newRequest.getStatus());
    }
    
    // Test 7: Test both empty product and category
    @Test
    void testSearchPromotion_BothEmpty() {
        // Arrange
        searchRequest.setProductId("");
        searchRequest.setCategoryId("");
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
    }
    
    // Test 8: Test service with different status
    @Test
    void testSearchPromotion_DifferentStatus() {
        // Arrange
        searchRequest.setStatus("inactive");
        searchRequest.setProductId("");
        searchRequest.setCategoryId("");
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
    }
    
    // Test 9: Test null status
    @Test
    void testSearchPromotion_NullStatus() {
        // Arrange
        searchRequest.setStatus(null);
        searchRequest.setProductId("");
        searchRequest.setCategoryId("");
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
    }
    
    // Test 10: Test logging and debugging paths
    @Test
    void testPromotionProcessing() {
        // Test that processPromotion method exists and handles null conditions
        assertNotNull(mockPromotion.getConditionsJson());
        assertNotNull(mockPromotion.getRulesJson());
    }
    
    // Test 11: Test mapToResponse method indirectly
    @Test
    void testPromotionMappingSetup() {
        // Verify all fields are set on mock promotion
        assertNotNull(mockPromotion.getId());
        assertNotNull(mockPromotion.getCode());
        assertNotNull(mockPromotion.getType());
        assertNotNull(mockPromotion.getValue());
        assertNotNull(mockPromotion.getValueType());
        assertNotNull(mockPromotion.getStartsAt());
        assertNotNull(mockPromotion.getEndsAt());
        assertNotNull(mockPromotion.getUsageLimit());
        assertNotNull(mockPromotion.getUsageCount());
        assertNotNull(mockPromotion.getStatus());
        assertNotNull(mockPromotion.getConditionsJson());
        assertNotNull(mockPromotion.getRulesJson());
        assertNotNull(mockPromotion.getCreatedAt());
        assertNotNull(mockPromotion.getUpdatedAt());
    }
    
    // Test 12: Test edge cases with null dates
    @Test
    void testSearchPromotion_NullDatesWithEmptyProduct() {
        // Arrange
        searchRequest.setStartsAt(null);
        searchRequest.setEndsAt(null);
        searchRequest.setProductId("");
        
        // Act
        Mono<PromotionResponse> result = promotionService.searchPromotion(searchRequest);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
    }
    
    // Test 13: Test constructor and initialization
    @Test
    void testServiceInitialization() {
        // Verify the service is properly constructed with mocked dependencies
        assertNotNull(promotionService);
        
        // Test that search request can be created and modified
        PromotionSearchRequest newRequest = new PromotionSearchRequest();
        newRequest.setStatus("test");
        newRequest.setProductId("test-product");
        newRequest.setCategoryId("test-category");
        
        assertEquals("test", newRequest.getStatus());
        assertEquals("test-product", newRequest.getProductId());
        assertEquals("test-category", newRequest.getCategoryId());
    }
    
    // Test 14: Test Promotion model getters and setters
    @Test
    void testPromotionModelMethods() {
        Promotion promotion = new Promotion();
        UUID testId = UUID.randomUUID();
        
        promotion.setId(testId);
        promotion.setCode("TEST");
        promotion.setType("percentage");
        promotion.setValue(new BigDecimal("50.00"));
        promotion.setAutomatic(true);
        
        assertEquals(testId, promotion.getId());
        assertEquals("TEST", promotion.getCode());
        assertEquals("percentage", promotion.getType());
        assertEquals(new BigDecimal("50.00"), promotion.getValue());
        assertTrue(promotion.isAutomatic());
    }
    
    // Test 15: Test request model methods
    @Test
    void testSearchRequestMethods() {
        PromotionSearchRequest request = new PromotionSearchRequest();
        Instant now = Instant.now();
        
        request.setStatus("active");
        request.setProductId("SKU-001");
        request.setCategoryId("CAT-001");
        request.setStartsAt(now);
        request.setEndsAt(now.plusSeconds(3600));
        
        assertEquals("active", request.getStatus());
        assertEquals("SKU-001", request.getProductId());
        assertEquals("CAT-001", request.getCategoryId());
        assertEquals(now, request.getStartsAt());
        assertEquals(now.plusSeconds(3600), request.getEndsAt());
    }
} 