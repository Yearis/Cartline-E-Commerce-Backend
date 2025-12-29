package com.yearis.e_commerce.payload.review.seller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellerReviewRequest {

    @NotNull
    @Min(0) @Max(5)
    private Integer deliveryRating;

    @NotNull
    @Min(0) @Max(5)
    private Integer productAccuracyRating;

    @NotNull
    @Min(0) @Max(5)
    private Integer serviceRating;

    @NotNull
    @Min(0) @Max(5)
    private Integer packagingRating;
}
