package com.promotion.engine.controller;

import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.dto.response.PromotionResponse;
import com.promotion.engine.service.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for the promotion controller.
 */
@ExtendWith(MockitoExtension.class)
public class PromotionControllerTest {

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private PromotionController controller;

    private PromotionResponse mockResponse;
    private Instant validStartDate;
    private Instant validEndDate;

    @BeforeEach
    void setUp() {
        // Setup mock response
        mockResponse = new PromotionResponse();
        mockResponse.setId(UUID.randomUUID());
        mockResponse.setCode("TEST_CODE");
        mockResponse.setType("fixed_amount");
        mockResponse.setValue(new BigDecimal("10.99"));
        mockResponse.setValueType("fixed_amount");
        mockResponse.setStatus("active");

        // Setup valid dates
        validStartDate = Instant.now().plusSeconds(86400); // tomorrow
        validEndDate = Instant.now().plusSeconds(2592000); // 30 days from now
    }

    @Test
    void testSearchPromotion_WithValidProductId() {
        // Arrange
        PromotionSearchRequest request = new PromotionSearchRequest();
        request.setStatus("active");
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(validStartDate);
        request.setEndsAt(validEndDate);

        when(promotionService.searchPromotion(any(PromotionSearchRequest.class)))
                .thenReturn(Mono.just(mockResponse));

        // Act
        Mono<PromotionResponse> result = controller.searchPromotions(request);

        // Assert
        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();
    }

    @Test
    void testSearchPromotion_WithValidCategoryId() {
        // Arrange
        PromotionSearchRequest request = new PromotionSearchRequest();
        request.setStatus("active");
        request.setCategoryId("SKU-CAT-001");
        request.setStartsAt(validStartDate);
        request.setEndsAt(validEndDate);

        when(promotionService.searchPromotion(any(PromotionSearchRequest.class)))
                .thenReturn(Mono.just(mockResponse));

        // Act
        Mono<PromotionResponse> result = controller.searchPromotions(request);

        // Assert
        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();
    }

    @Test
    void testSearchPromotion_WithNoResults() {
        // Arrange
        PromotionSearchRequest request = new PromotionSearchRequest();
        request.setStatus("active");
        request.setProductId("NONEXISTENT-ID");
        request.setStartsAt(validStartDate);
        request.setEndsAt(validEndDate);

        when(promotionService.searchPromotion(any(PromotionSearchRequest.class)))
                .thenReturn(Mono.empty());

        // Act
        Mono<PromotionResponse> result = controller.searchPromotions(request);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
} 