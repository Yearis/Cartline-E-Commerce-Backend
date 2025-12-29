package com.yearis.e_commerce.payload.review.product;

import lombok.Data;

@Data
public class ProductReviewResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Integer rating;

    private String comment;

    private Long helpfulCounter = 0L;

    private Boolean verifiedPurchase;
}
