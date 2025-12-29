package com.yearis.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_prod_rev", columnNames = {"user_id", "product_id"})
})
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(0)
    @Max(5)
    @Column(name = "rating")
    private Integer rating;

    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500")
    @Column(name = "comment", nullable = false)
    private String comment;

    @ElementCollection
    @CollectionTable(
            name = "product_review_helpful_users",
            joinColumns = @JoinColumn(name = "review_id")
    )
    @Column(name = "user_id")
    private Set<Long> helpfulUserIds = new HashSet<>();

    @Column(name = "helpful_counter")
    private Long helpfulCounter = 0L;

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
