package com.promotion.engine.service;

import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.dto.response.PromotionResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for promotion-related operations.
 */
public interface PromotionService {

    /**
     * Searches for a promotion based on the provided criteria.
     * Implements fallback lookup: first searches promotion_engine_v1, then promotiondb.
     *
     * @param request The search criteria
     * @return A mono containing the matching promotion, or empty if not found
     */
    Mono<PromotionResponse> searchPromotion(PromotionSearchRequest request);
} 