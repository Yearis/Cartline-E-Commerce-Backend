package com.yearis.e_commerce.service.review.seller;

import com.yearis.e_commerce.payload.review.seller.SellerReviewRequest;
import com.yearis.e_commerce.payload.review.seller.SellerReviewResponse;

import java.util.List;

public interface SellerReviewService {

    SellerReviewResponse reviewSeller(Long sellerId, SellerReviewRequest reviewRequest);

    SellerReviewResponse getSellerReview(Long reviewId, Long sellerId);

    List<SellerReviewResponse> getSellerReviews(Long sellerId, int pageNo, int pageSize);

    SellerReviewResponse updateSellerReview(Long sellerId, Long reviewId, SellerReviewRequest reviewRequest);

    void deleteSellerReview(Long sellerId, Long reviewId);
}
