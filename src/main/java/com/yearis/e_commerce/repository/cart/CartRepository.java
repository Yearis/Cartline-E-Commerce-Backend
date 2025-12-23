package com.yearis.e_commerce.repository.cart;

import com.yearis.e_commerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
