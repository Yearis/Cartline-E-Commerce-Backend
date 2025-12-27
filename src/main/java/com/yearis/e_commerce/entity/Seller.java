package com.yearis.e_commerce.entity;

import com.yearis.e_commerce.enums.SellerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Seller {

    @Id
    private Long id;

    private String storeName;

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

    private SellerStatus sellerStatus;

    // Relationships:

    // 1 seller -> 1 user
    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 1 seller -> many products
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private Set<Product> products;
}
