package com.yearis.e_commerce.payload.review.seller;

import lombok.Data;

@Data
public class SellerReviewResponse {

    private Long id;

    private Long sellerId;

    private String sellerName;

    private Double overallRating;

    private Integer deliveryRating;

    private Integer productAccuracyRating;

    private Integer serviceRating;

    private Integer packagingRating;
}
