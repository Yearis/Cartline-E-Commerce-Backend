package com.yearis.e_commerce.service.review.seller;

import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.entity.SellerReview;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.exception.ActionNotAllowedException;
import com.yearis.e_commerce.exception.ResourceAlreadyExistsException;
import com.yearis.e_commerce.exception.ReviewNotFoundException;
import com.yearis.e_commerce.exception.SellerNotFoundException;
import com.yearis.e_commerce.payload.review.seller.SellerReviewRequest;
import com.yearis.e_commerce.payload.review.seller.SellerReviewResponse;
import com.yearis.e_commerce.repository.review.seller.SellerReviewRepository;
import com.yearis.e_commerce.repository.seller.SellerRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerReviewServiceImpl implements SellerReviewService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final SellerReviewRepository sellerReviewRepository;

    private User currentUser() {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // --- Mappers ---
    private SellerReviewResponse mapToResponse(SellerReview review) {

        SellerReviewResponse response = new SellerReviewResponse();
        response.setId(review.getId());
        response.setSellerId(review.getSeller().getId());
        response.setSellerName(review.getSeller().getStoreName());
        response.setServiceRating(review.getServiceRating());
        response.setDeliveryRating(review.getDeliveryRating());
        response.setPackagingRating(review.getPackagingRating());
        response.setProductAccuracyRating(review.getProductAccuracyRating());
        response.setOverallRating(review.getOverallRating());

        return response;
    }

    private SellerReview mapToEntity(SellerReviewRequest reviewRequest) {

        SellerReview review = new SellerReview();
        review.setServiceRating(reviewRequest.getServiceRating());
        review.setDeliveryRating(reviewRequest.getDeliveryRating());
        review.setPackagingRating(reviewRequest.getPackagingRating());
        review.setProductAccuracyRating(reviewRequest.getProductAccuracyRating());
        review.calculateOverallRating();

        return review;
    }

    @Override
    @Transactional
    public SellerReviewResponse reviewSeller(Long sellerId, SellerReviewRequest reviewRequest) {

        User currentUser = currentUser();

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));

        // we check if user has already reviewed seller or not
        if (sellerReviewRepository.existsByUserIdAndSellerId(currentUser.getId(), sellerId)) {
            throw new ResourceAlreadyExistsException("Review already exists");
        }

        // add user and seller to review
        SellerReview review = mapToEntity(reviewRequest);
        review.setUser(currentUser);
        review.setSeller(seller);

        // add review in current user's
        currentUser.getSellerReviews().add(review);

        // add review in seller
        seller.getReviews().add(review);
        seller.calculateAverageRating();

        SellerReview savedReview = sellerReviewRepository.save(review);

        User savedUser = userRepository.save(currentUser);
        Seller savedSeller = sellerRepository.save(seller);

        return mapToResponse(savedReview);
    }

    @Override
    public SellerReviewResponse getSellerReview(Long reviewId, Long sellerId) {

        SellerReview review = sellerReviewRepository.findByIdAndSellerId(reviewId, sellerId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        return mapToResponse(review);
    }

    @Override
    public List<SellerReviewResponse> getSellerReviews(Long sellerId, int pageNo, int pageSize) {

        Sort sort = Sort.by("overallRating").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<SellerReview> reviewPage = sellerReviewRepository.findBySellerId(sellerId, pageable);

        return reviewPage.getContent().stream()
                .map(review -> mapToResponse(review))
                .toList();
    }

    @Override
    @Transactional
    public SellerReviewResponse updateSellerReview(Long sellerId, Long reviewId, SellerReviewRequest reviewRequest) {

        User currentUser = currentUser();

        // we check if user has already reviewed seller or not
        SellerReview review = sellerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review doesn't exist"));

        if (!review.getUser().getId().equals(currentUser.getId()) ||
                !review.getSeller().getId().equals(sellerId)) {
            throw new ActionNotAllowedException("You are not authorized to update this review");
        }

       boolean isUpdated = false;

       // now we check what field user has sent to update

        // if its service rating
        if (reviewRequest.getServiceRating() != null) {

            // if service rating is not equal to its previous rating we update
            if (!reviewRequest.getServiceRating().equals(review.getServiceRating())) {
                review.setServiceRating(reviewRequest.getServiceRating());
                isUpdated = true;
            }
        }

        // if its delivery rating
        if (reviewRequest.getDeliveryRating() != null) {

            // if delivery rating is not equal to its previous rating we update
            if (!reviewRequest.getDeliveryRating().equals(review.getDeliveryRating())) {
                review.setDeliveryRating(reviewRequest.getDeliveryRating());
                isUpdated = true;
            }
        }

        // if its packaging rating
        if (reviewRequest.getPackagingRating() != null) {

            // if packaging rating is not equal to its previous rating we update
            if (!reviewRequest.getPackagingRating().equals(review.getPackagingRating())) {
                review.setPackagingRating(reviewRequest.getPackagingRating());
                isUpdated = true;
            }
        }

        // if its product accuracy rating
        if (reviewRequest.getProductAccuracyRating() != null) {

            // if product accuracy rating is not equal to its previous rating we update
            if (!reviewRequest.getProductAccuracyRating().equals(review.getProductAccuracyRating())) {
                review.setProductAccuracyRating(reviewRequest.getProductAccuracyRating());
                isUpdated = true;
            }
        }

        // if rating is changed we re-calculate the overall rating and save it
        if (isUpdated) {

            review.calculateOverallRating();
            review.getSeller().calculateAverageRating();
            sellerRepository.save(currentUser.getSeller());

            return mapToResponse(sellerReviewRepository.save(review));
        }

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public void deleteSellerReview(Long sellerId, Long reviewId) {

        User currentUser = currentUser();

        // we check if user has already reviewed seller or not
        SellerReview review = sellerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review doesn't exist"));

        if (!review.getUser().getId().equals(currentUser.getId()) ||
                !review.getSeller().getId().equals(sellerId)) {
            throw new ActionNotAllowedException("You are not authorized to delete this review");
        }

        Seller seller = review.getSeller();

        // we remove from seller, user and re-calculate the rating of seller
        seller.getReviews().remove(review);
        seller.calculateAverageRating();
        sellerRepository.save(seller);

        currentUser.getSellerReviews().remove(review);
        userRepository.save(currentUser);

        sellerReviewRepository.delete(review);
    }
}
