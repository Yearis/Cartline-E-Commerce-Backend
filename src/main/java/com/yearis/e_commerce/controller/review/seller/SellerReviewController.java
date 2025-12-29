package com.yearis.e_commerce.controller.review.seller;

import com.yearis.e_commerce.payload.review.seller.SellerReviewRequest;
import com.yearis.e_commerce.payload.review.seller.SellerReviewResponse;
import com.yearis.e_commerce.service.review.seller.SellerReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Seller Review REST API", description = "Operations related to seller reviews")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sellers/{sellerId}/reviews")
public class SellerReviewController {

    private final SellerReviewService sellerReviewService;

    @Operation(summary = "Review a Seller", description = "User adds a review for a seller")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<SellerReviewResponse> reviewSeller(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId,
            @Parameter(description = "Review details payload") @Valid @RequestBody SellerReviewRequest request) {

        SellerReviewResponse response = sellerReviewService.reviewSeller(sellerId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Reviews for Seller", description = "Get reviews for a seller")
    @GetMapping
    public ResponseEntity<List<SellerReviewResponse>> getSellerReviews(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<SellerReviewResponse> responses = sellerReviewService.getSellerReviews(sellerId, pageNo, pageSize);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Get Review for a seller", description = "Get review for a seller by ID")
    // Public Access
    @GetMapping("/{reviewId}")
    public ResponseEntity<SellerReviewResponse> getSellerReview(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId) {

        SellerReviewResponse response = sellerReviewService.getSellerReview(reviewId, sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Seller Review", description = "Update an existing review")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<SellerReviewResponse> updateSellerReview(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId,
            @Parameter(description = "Updated details payload") @Valid @RequestBody SellerReviewRequest request) {

        SellerReviewResponse response = sellerReviewService.updateSellerReview(sellerId, reviewId, request)
                ;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete Seller Review", description = "Delete a review")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteSellerReview(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId) {

        sellerReviewService.deleteSellerReview(sellerId, reviewId);

        return new ResponseEntity<>("Review deleted successfully.", HttpStatus.OK);
    }
}