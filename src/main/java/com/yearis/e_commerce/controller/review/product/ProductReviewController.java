package com.yearis.e_commerce.controller.review.product;

import com.yearis.e_commerce.payload.review.product.ProductReviewRequest;
import com.yearis.e_commerce.payload.review.product.ProductReviewResponse;
import com.yearis.e_commerce.service.review.product.ProductReviewService;
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

@Tag(name = "Product Review REST API", description = "Operations related to product reviews")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @Operation(summary = "Add a Review", description = "User adds a review for a product")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ProductReviewResponse> addReview(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "Review payload") @Valid @RequestBody ProductReviewRequest request) {

        ProductReviewResponse response = productReviewService.reviewProduct(productId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get All Reviews", description = "Get reviews product")
    @GetMapping
    public ResponseEntity<List<ProductReviewResponse>> getProductReviews(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductReviewResponse> responses = productReviewService.getProductReviews(productId, pageNo, pageSize);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Get a Review by ID", description = "Get review by its ID")
    // Public access
    @GetMapping("/{reviewId}")
    public ResponseEntity<ProductReviewResponse> getReviewById(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId) {

        ProductReviewResponse response = productReviewService.getProductReview(reviewId, productId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Review", description = "Update an existing review")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewResponse> updateReview(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId,
            @Parameter(description = "Updated details payload") @Valid @RequestBody ProductReviewRequest request) {

        ProductReviewResponse response = productReviewService.updateProductReview(productId, reviewId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete Review", description = "Delete an existing review")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId) {

        productReviewService.deleteProductReview(productId, reviewId);

        return new ResponseEntity<>("Review deleted successfully.", HttpStatus.OK);
    }

    @Operation(summary = "Mark as Helpful", description = "Vote 'Helpful on a review")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}/helpful")
    public ResponseEntity<ProductReviewResponse> markReviewAsHelpful(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "ID of the review") @PathVariable Long reviewId) {

        ProductReviewResponse response = productReviewService.markReviewAsHelpful(productId, reviewId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}