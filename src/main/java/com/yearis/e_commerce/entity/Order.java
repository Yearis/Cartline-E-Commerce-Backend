package com.yearis.e_commerce.entity;

import com.yearis.e_commerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateAndTime;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "shipping_address_line1", nullable = false)),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2")),
            @AttributeOverride(name = "landmark", column = @Column(name = "shipping_landmark")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country", nullable = false)),
            @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip_code", nullable = false))
    })
    private Address shippingAddress;

    // Relationships:

    // Many orders -> 1 user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    // 1 order -> many items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
}
