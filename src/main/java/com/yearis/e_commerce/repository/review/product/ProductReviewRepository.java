package com.yearis.e_commerce.repository.review.product;

import com.yearis.e_commerce.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<ProductReview> findByIdAndProductId(Long reviewId, Long productId);

    Page<ProductReview> findByProductId(Long productId, Pageable pageable);
}
