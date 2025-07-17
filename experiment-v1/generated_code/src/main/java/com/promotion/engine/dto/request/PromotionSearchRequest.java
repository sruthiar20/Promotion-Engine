package com.promotion.engine.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.promotion.engine.validation.ValidPromotion;

import java.time.Instant;

/**
 * DTO for promotion search requests.
 */
@ValidPromotion
public class PromotionSearchRequest {
    
    private String status;
    
    @JsonProperty("starts_at")
    private Instant startsAt;
    
    @JsonProperty("ends_at")
    private Instant endsAt;
    
    @JsonProperty("product-id")
    private String productId;
    
    @JsonProperty("category-id")
    private String categoryId;
    
    // Getters and setters
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Instant getStartsAt() {
        return startsAt;
    }
    
    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }
    
    public Instant getEndsAt() {
        return endsAt;
    }
    
    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
} 