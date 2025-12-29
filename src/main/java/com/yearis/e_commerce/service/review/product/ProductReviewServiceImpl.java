package com.yearis.e_commerce.service.review.product;

import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.entity.ProductReview;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.exception.ActionNotAllowedException;
import com.yearis.e_commerce.exception.ProductNotFoundException;
import com.yearis.e_commerce.exception.ResourceAlreadyExistsException;
import com.yearis.e_commerce.exception.ReviewNotFoundException;
import com.yearis.e_commerce.payload.review.product.ProductReviewRequest;
import com.yearis.e_commerce.payload.review.product.ProductReviewResponse;
import com.yearis.e_commerce.repository.order.OrderRepository;
import com.yearis.e_commerce.repository.product.ProductRepository;
import com.yearis.e_commerce.repository.review.product.ProductReviewRepository;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReviewServiceImpl implements ProductReviewService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final OrderRepository orderRepository;

    private User currentUser() {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // --- Mappers ---
    private ProductReviewResponse mapToResponse(ProductReview review) {

        ProductReviewResponse response = new ProductReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProduct().getId());
        response.setProductName(review.getProduct().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setHelpfulCounter(review.getHelpfulCounter());
        response.setVerifiedPurchase(review.getVerifiedPurchase());

        return response;
    }

    private ProductReview mapToEntity(ProductReviewRequest request) {

        ProductReview review = new ProductReview();
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return review;
    }


    @Override
    @Transactional
    public ProductReviewResponse reviewProduct(Long productId, ProductReviewRequest reviewRequest) {

        User currentUser = currentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // we check if user has already reviewed the product or not
        if (productReviewRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new ResourceAlreadyExistsException("Review already exists");
        }

        ProductReview review = mapToEntity(reviewRequest);
        review.setUser(currentUser);
        review.setProduct(product);

        // now we check if user has even bought the product or not
        boolean hasPurchased = orderRepository.existsByUserIdAndProductId(currentUser.getId(), productId);
        review.setVerifiedPurchase(hasPurchased);

        currentUser.getProductReviews().add(review);

        product.getReviews().add(review);
        product.calculateAverageRating();

        ProductReview savedReview = productReviewRepository.save(review);

        User savedUser = userRepository.save(currentUser);
        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional
    public ProductReviewResponse markReviewAsHelpful(Long productId, Long reviewId) {

        User currentUser = currentUser();

        ProductReview review = productReviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        Set<Long> helpfulUsers = review.getHelpfulUserIds();

        if (helpfulUsers.contains(currentUser.getId())) {
            // if user has voted helpful already 2nd click means unlike
            helpfulUsers.remove(currentUser.getId());
            review.setHelpfulCounter((long) helpfulUsers.size());
        } else {
            // if user has not voted then it likes
            helpfulUsers.add(currentUser.getId());
            review.setHelpfulCounter((long) helpfulUsers.size());
        }

        ProductReview savedReview = productReviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Override
    public ProductReviewResponse getProductReview(Long reviewId, Long productId) {

        ProductReview review = productReviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        return mapToResponse(review);
    }

    @Override
    public List<ProductReviewResponse> getProductReviews(Long productId, int pageNo, int pageSize) {

        Sort sort = Sort.by("rating").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductReview> reviewPage = productReviewRepository.findByProductId(productId, pageable);

        return reviewPage.getContent().stream()
                .map(review -> mapToResponse(review))
                .toList();
    }

    @Override
    @Transactional
    public ProductReviewResponse updateProductReview(Long productId, Long reviewId, ProductReviewRequest reviewRequest) {

        User currentUser = currentUser();

        ProductReview review = productReviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(currentUser.getId()) ||
                !review.getProduct().getId().equals(productId)) {
            throw new ActionNotAllowedException("You are not authorized to update this review");
        }

        boolean isUpdated = false;

        // now we check what field user has sent to update

        // if its rating
        if (reviewRequest.getRating() != null) {

            // if rating is not equal to its previous rating we update
            if (!reviewRequest.getRating().equals(review.getRating())) {
                review.setRating(reviewRequest.getRating());
                isUpdated = true;
            }
        }

        // if its comment
        if (reviewRequest.getComment() != null) {

            // if comment is not equal to its previous comment we update
            if (!reviewRequest.getComment().equals(review.getComment())) {
                review.setComment(reviewRequest.getComment());
                isUpdated = true;
            }
        }

        // if review is changed we save it
        if (isUpdated) {

            review.getProduct().calculateAverageRating();
            productRepository.save(review.getProduct());

            return mapToResponse(productReviewRepository.save(review));
        }

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public void deleteProductReview(Long productId, Long reviewId) {

        User currentUser = currentUser();

        ProductReview review = productReviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(currentUser.getId()) ||
                !review.getProduct().getId().equals(productId)) {
            throw new ActionNotAllowedException("You are not authorized to delete this review");
        }

        Product product = review.getProduct();
        product.getReviews().remove(review);
        product.calculateAverageRating();
        productRepository.save(product);

        currentUser.getProductReviews().remove(review);
        userRepository.save(currentUser);

        productReviewRepository.delete(review);
    }
}
