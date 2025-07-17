package com.promotion.engine.repository;

import com.promotion.engine.model.Promotion;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for accessing promotions in the fallback promotiondb database.
 */
@Repository
public interface FallbackPromotionRepository extends ReactiveCrudRepository<Promotion, UUID> {
    
    /**
     * Finds promotions with product condition matching the given product ID in fallback database.
     *
     * @param status    The status to search for
     * @param productId The product ID to search for
     * @param startsAt  Optional start date
     * @param endsAt    Optional end date
     * @return A flux of matching promotions
     */
    @Query("SELECT * FROM promotion WHERE status = :status " +
           "AND conditions_json::text LIKE '%\"type\":\"product\"%' " +
           "AND conditions_json::text LIKE '%\"' || :productId || '\"%' " +
           "AND (:startsAt IS NULL OR starts_at >= :startsAt) " +
           "AND (:endsAt IS NULL OR ends_at <= :endsAt)")
    Flux<Promotion> findByStatusAndProductId(String status, String productId, Instant startsAt, Instant endsAt);
    
    /**
     * Finds promotions with category condition matching the given category ID in fallback database.
     *
     * @param status     The status to search for
     * @param categoryId The category ID to search for
     * @param startsAt   Optional start date
     * @param endsAt     Optional end date
     * @return A flux of matching promotions
     */
    @Query("SELECT * FROM promotion WHERE status = :status " +
           "AND conditions_json::text LIKE '%\"type\":\"category\"%' " +
           "AND conditions_json::text LIKE '%\"' || :categoryId || '\"%' " +
           "AND (:startsAt IS NULL OR starts_at >= :startsAt) " +
           "AND (:endsAt IS NULL OR ends_at <= :endsAt)")
    Flux<Promotion> findByStatusAndCategoryId(String status, String categoryId, Instant startsAt, Instant endsAt);
} 