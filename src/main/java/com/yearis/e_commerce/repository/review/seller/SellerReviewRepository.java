package com.yearis.e_commerce.repository.review.seller;

import com.yearis.e_commerce.entity.SellerReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerReviewRepository extends JpaRepository<SellerReview, Long> {

    boolean existsByUserIdAndSellerId(Long userId, Long sellerId);

    Optional<SellerReview> findByIdAndSellerId(Long id, Long sellerId);

    Page<SellerReview> findBySellerId(Long sellerId, Pageable pageable);

    Optional<SellerReview> findByUserIdAndSellerId(Long id, Long sellerId);
}
