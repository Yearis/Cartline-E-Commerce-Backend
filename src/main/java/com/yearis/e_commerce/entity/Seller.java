package com.yearis.e_commerce.entity;

import com.yearis.e_commerce.enums.SellerStatus;
import jakarta.persistence.*;
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
@Table(name = "sellers")
public class Seller {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "store_name", nullable = false, unique = true)
    private String storeName;

    @Column(name = "business_phone_number", nullable = false, unique = true)
    private String businessPhoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "business_address_line1", nullable = false)),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "business_address_line2")),
            @AttributeOverride(name = "landmark", column = @Column(name = "business_landmark")),
            @AttributeOverride(name = "city", column = @Column(name = "business_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "business_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "business_country", nullable = false)),
            @AttributeOverride(name = "zipCode", column = @Column(name = "business_zip_code", nullable = false))
    })
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_status")
    private SellerStatus sellerStatus;

    @Column(name = "average_rating")
    private Double averageRating;

    // Relationships:

    // 1 seller -> 1 user
    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 1 seller -> many products
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    // 1 seller -> many reviews
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("overallRating DESC")
    Set<SellerReview> reviews = new HashSet<>();

    public void calculateAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            this.averageRating = 0.0;
            return;
        }
        this.averageRating = reviews.stream()
                .mapToDouble(sellerReview -> sellerReview.getOverallRating())
                .average()
                .orElse(0.0);
    }
}
