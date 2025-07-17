package com.promotion.engine.controller;

import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.dto.response.PromotionResponse;
import com.promotion.engine.service.PromotionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller for promotion related endpoints.
 */
@RestController
@RequestMapping("/admin/promotions")
public class PromotionController {

    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);
    
    private final PromotionService promotionService;

    /**
     * Creates a new promotion controller.
     *
     * @param promotionService The promotion service
     */
    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Searches for promotions by ID and other criteria.
     *
     * @param request The promotion search request
     * @return A mono of matching promotion
     */
    @GetMapping("/searchById")
    public Mono<PromotionResponse> searchPromotions(@Valid @RequestBody PromotionSearchRequest request) {
        logger.info("Received request to search promotions with request: {}", request);
        
        return promotionService.searchPromotion(request);
    }
} 