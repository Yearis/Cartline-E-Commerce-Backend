package com.yearis.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_reviews")
public class ProductionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(0)
    @Max(5)
    @Column(name = "rating")
    private Integer rating;

    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500")
    @Column(comment = "comment")
    private String comment;

    @Column(name = "helpful_counter")
    private Long helpfulCounter;

    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase = false;

    // Relationships:

    // Many reviews -> 1 product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Many review -> 1 user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
