package com.yearis.e_commerce.payload.review.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductReviewRequest {

    @Min(0)
    @Max(5)
    @NotNull(message = "Rating is mandatory")
    private Integer rating;

    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500")
    private String comment;
}
