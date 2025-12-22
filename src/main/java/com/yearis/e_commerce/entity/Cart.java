package com.yearis.e_commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // later we can add service fee or extra items

    // Relationships:

    /*
         1 cart -> 1 user
         @OneToOne
         @JoinColumn(name = "user_id")
         private User user;
    */

    // 1 cart -> Many items
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) // so that when cart deletes everything is deleted
    private Set<CartItem> cartItems = new HashSet<>();
}
