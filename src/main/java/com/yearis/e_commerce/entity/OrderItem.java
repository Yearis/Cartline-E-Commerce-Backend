package com.yearis.e_commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // Relationships:

    // Many items -> 1 product (e.g. 3 Apple Watch -> Apple Watch)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Many items -> 1 order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public void setTotalPrice() {

        // we will set our unit price as our discounted price
        this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
    }
}
