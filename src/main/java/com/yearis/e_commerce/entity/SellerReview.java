package com.yearis.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "seller_reviews")
public class SellerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(0)
    @Max(5)
    @Column(name = "overall_rating")
    private Double overallRating;

    @Min(0)
    @Max(5)
    @Column(name = "delivery_rating")
    private Integer deliveryRating;

    @Min(0)
    @Max(5)
    @Column(name = "product_accuracy_rating")
    private Integer productAccuracyRating;

    @Min(0)
    @Max(5)
    @Column(name = "service_rating")
    private Integer serviceRating;

    @Min(0)
    @Max(5)
    @Column(name = "packaging_rating")
    private Integer packagingRating;

    // Relationships:

    // Many reviews -> 1 seller
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    // Many review -> 1 user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void calculateOverallRating() {

        double sum = deliveryRating + productAccuracyRating + serviceRating + packagingRating;
        this.overallRating = sum / 4.0;
    }
}
