package com.promotion.engine.service.impl;

import com.promotion.engine.config.JsonNodeConverter;
import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.dto.response.PromotionResponse;
import com.promotion.engine.exception.DateFormatException;
import com.promotion.engine.exception.PromotionNotFoundException;
import com.promotion.engine.model.Promotion;
import com.promotion.engine.repository.PromotionRepository;
import com.promotion.engine.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeParseException;

/**
 * Implementation of the promotion service with fallback lookup functionality.
 * Primary lookup: promotion_engine_v1 database
 * Fallback lookup: promotiondb database
 */
@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceImpl.class);
    
    private final PromotionRepository promotionRepository;
    private final R2dbcEntityTemplate fallbackR2dbcTemplate;
    private final JsonNodeConverter jsonNodeConverter;

    /**
     * Creates a new promotion service.
     *
     * @param promotionRepository The primary promotion repository
     * @param fallbackR2dbcTemplate The fallback database template
     * @param jsonNodeConverter The JSON converter
     */
    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository, 
                               @Qualifier("fallbackR2dbcTemplate") R2dbcEntityTemplate fallbackR2dbcTemplate,
                               JsonNodeConverter jsonNodeConverter) {
        this.promotionRepository = promotionRepository;
        this.fallbackR2dbcTemplate = fallbackR2dbcTemplate;
        this.jsonNodeConverter = jsonNodeConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PromotionResponse> searchPromotion(PromotionSearchRequest request) {
        logger.debug("Searching for promotion with request: {}", request);
        
        try {
            if (request.getProductId() != null && !request.getProductId().isEmpty()) {
                logger.debug("Searching by product ID: {}", request.getProductId());
                return searchByProductId(request);
            } else if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
                logger.debug("Searching by category ID: {}", request.getCategoryId());
                return searchByCategoryId(request);
            } else {
                logger.error("Invalid request: neither product ID nor category ID provided");
                return Mono.empty();
            }
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new DateFormatException(
                e.getMessage().contains("starts_at") ? "starts_at" : "ends_at",
                "Invalid date format"
            );
        }
    }
    
    /**
     * Searches for promotions by product ID with fallback logic.
     *
     * @param request The search request
     * @return Mono of promotion response
     */
    private Mono<PromotionResponse> searchByProductId(PromotionSearchRequest request) {
        return promotionRepository.findByStatusAndProductId(
                request.getStatus(),
                request.getProductId(),
                request.getStartsAt(),
                request.getEndsAt()
            )
            .next() // Get first result from primary database
            .map(this::processPromotion)
            .map(this::mapToResponse)
            .doOnNext(result -> logger.debug("Found promotion in primary database: {}", result.getId()))
            .switchIfEmpty(
                // Fallback to promotiondb if not found in primary
                searchInFallbackByProductId(request)
                    .doOnNext(result -> logger.debug("Found promotion in fallback database: {}", result.getId()))
                    .doOnSubscribe(s -> logger.debug("Primary database search returned empty, searching fallback database"))
            )
            .switchIfEmpty(
                // If not found in either database, throw exception
                Mono.error(new PromotionNotFoundException("product-id", request.getProductId()))
            );
    }
    
    /**
     * Searches for promotions by category ID with fallback logic.
     *
     * @param request The search request
     * @return Mono of promotion response
     */
    private Mono<PromotionResponse> searchByCategoryId(PromotionSearchRequest request) {
        return promotionRepository.findByStatusAndCategoryId(
                request.getStatus(),
                request.getCategoryId(),
                request.getStartsAt(),
                request.getEndsAt()
            )
            .next() // Get first result from primary database
            .map(this::processPromotion)
            .map(this::mapToResponse)
            .doOnNext(result -> logger.debug("Found promotion in primary database: {}", result.getId()))
            .switchIfEmpty(
                // Fallback to promotiondb if not found in primary
                searchInFallbackByCategoryId(request)
                    .doOnNext(result -> logger.debug("Found promotion in fallback database: {}", result.getId()))
                    .doOnSubscribe(s -> logger.debug("Primary database search returned empty, searching fallback database"))
            )
            .switchIfEmpty(
                // If not found in either database, throw exception
                Mono.error(new PromotionNotFoundException("category-id", request.getCategoryId()))
            );
    }
    
    /**
     * Searches in fallback database by product ID.
     *
     * @param request The search request
     * @return Mono of promotion response
     */
    private Mono<PromotionResponse> searchInFallbackByProductId(PromotionSearchRequest request) {
        Criteria criteria = Criteria.where("status").is(request.getStatus())
            .and(Criteria.where("conditions_json").like("%\"type\":\"product\"%"))
            .and(Criteria.where("conditions_json").like("%\"" + request.getProductId() + "\"%"));
            
        if (request.getStartsAt() != null) {
            criteria = criteria.and(Criteria.where("ends_at").greaterThanOrEquals(request.getStartsAt()));
        }
        
        if (request.getEndsAt() != null) {
            criteria = criteria.and(Criteria.where("starts_at").lessThanOrEquals(request.getEndsAt()));
        }
        
        return fallbackR2dbcTemplate.select(Promotion.class)
            .from("promotion")
            .matching(Query.query(criteria))
            .first()
            .map(this::processPromotion)
            .map(this::mapToResponse);
    }
    
    /**
     * Searches in fallback database by category ID.
     *
     * @param request The search request
     * @return Mono of promotion response
     */
    private Mono<PromotionResponse> searchInFallbackByCategoryId(PromotionSearchRequest request) {
        Criteria criteria = Criteria.where("status").is(request.getStatus())
            .and(Criteria.where("conditions_json").like("%\"type\":\"category\"%"))
            .and(Criteria.where("conditions_json").like("%\"" + request.getCategoryId() + "\"%"));
            
        if (request.getStartsAt() != null) {
            criteria = criteria.and(Criteria.where("ends_at").greaterThanOrEquals(request.getStartsAt()));
        }
        
        if (request.getEndsAt() != null) {
            criteria = criteria.and(Criteria.where("starts_at").lessThanOrEquals(request.getEndsAt()));
        }
        
        return fallbackR2dbcTemplate.select(Promotion.class)
            .from("promotion")
            .matching(Query.query(criteria))
            .first()
            .map(this::processPromotion)
            .map(this::mapToResponse);
    }
    
    /**
     * Process a promotion by converting JSON strings to JsonNode objects.
     *
     * @param promotion The promotion to process
     * @return The processed promotion
     */
    private Promotion processPromotion(Promotion promotion) {
        if (promotion.getConditionsJson() != null) {
            promotion.setConditions(jsonNodeConverter.convertToJsonNode(promotion.getConditionsJson()));
        }
        
        if (promotion.getRulesJson() != null) {
            promotion.setRules(jsonNodeConverter.convertToJsonNode(promotion.getRulesJson()));
        }
        
        return promotion;
    }
    
    /**
     * Maps a promotion entity to a response DTO.
     *
     * @param promotion The promotion entity
     * @return The response DTO
     */
    private PromotionResponse mapToResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setCode(promotion.getCode());
        response.setType(promotion.getType());
        response.setValue(promotion.getValue());
        response.setValueType(promotion.getValueType());
        response.setStartsAt(promotion.getStartsAt());
        response.setEndsAt(promotion.getEndsAt());
        response.setAutomatic(promotion.isAutomatic());
        response.setUsageLimit(promotion.getUsageLimit());
        response.setUsageCount(promotion.getUsageCount());
        response.setStatus(promotion.getStatus());
        response.setConditions(promotion.getConditions());
        response.setRules(promotion.getRules());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setUpdatedAt(promotion.getUpdatedAt());
        return response;
    }
} 