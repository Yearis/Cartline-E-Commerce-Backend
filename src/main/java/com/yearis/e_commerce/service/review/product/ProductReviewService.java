package com.yearis.e_commerce.service.review.product;

import com.yearis.e_commerce.payload.review.product.ProductReviewRequest;
import com.yearis.e_commerce.payload.review.product.ProductReviewResponse;

import java.util.List;

public interface ProductReviewService {

    ProductReviewResponse reviewProduct(Long productId, ProductReviewRequest reviewRequest);

    ProductReviewResponse markReviewAsHelpful(Long productId, Long reviewId);

    ProductReviewResponse getProductReview(Long reviewId, Long productId);

    List<ProductReviewResponse> getProductReviews(Long productId, int pageNo, int pageSize);

    ProductReviewResponse updateProductReview(Long productId, Long reviewId, ProductReviewRequest reviewRequest);

    void deleteProductReview(Long productId, Long reviewId);
}
