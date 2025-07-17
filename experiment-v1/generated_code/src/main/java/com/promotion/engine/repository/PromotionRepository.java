package com.promotion.engine.repository;

import com.promotion.engine.model.Promotion;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for accessing promotions in the primary promotion_engine_v1 database.
 */
@Repository
public interface PromotionRepository extends ReactiveCrudRepository<Promotion, UUID> {
    
    /**
     * Finds promotions by status.
     *
     * @param status The status to search for
     * @return A flux of matching promotions
     */
    Flux<Promotion> findByStatus(String status);
    
    /**
     * Finds promotions with product condition matching the given product ID.
     * Searches for documents where the conditions array contains an object with:
     * - "type": "product"
     * - "value" array includes the specified product_id
     *
     * @param status    The status to search for
     * @param productId The product ID to search for
     * @param startsAt  Optional start date - find promotions active from this date
     * @param endsAt    Optional end date - find promotions active until this date
     * @return A flux of matching promotions
     */
    @Query("SELECT * FROM promotions WHERE status = :status " +
           "AND conditions_json::text LIKE '%\"type\":\"product\"%' " +
           "AND conditions_json::text LIKE '%\"' || :productId || '\"%' " +
           "AND (:startsAt IS NULL OR ends_at >= :startsAt) " +
           "AND (:endsAt IS NULL OR starts_at <= :endsAt)")
    Flux<Promotion> findByStatusAndProductId(String status, String productId, Instant startsAt, Instant endsAt);
    
    /**
     * Finds promotions with category condition matching the given category ID.
     * Searches for documents where the conditions array contains an object with:
     * - "type": "category"
     * - "value" array includes the specified category_id
     *
     * @param status     The status to search for
     * @param categoryId The category ID to search for
     * @param startsAt   Optional start date - find promotions active from this date
     * @param endsAt     Optional end date - find promotions active until this date
     * @return A flux of matching promotions
     */
    @Query("SELECT * FROM promotions WHERE status = :status " +
           "AND conditions_json::text LIKE '%\"type\":\"category\"%' " +
           "AND conditions_json::text LIKE '%\"' || :categoryId || '\"%' " +
           "AND (:startsAt IS NULL OR ends_at >= :startsAt) " +
           "AND (:endsAt IS NULL OR starts_at <= :endsAt)")
    Flux<Promotion> findByStatusAndCategoryId(String status, String categoryId, Instant startsAt, Instant endsAt);
} 