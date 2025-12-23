package com.yearis.e_commerce.repository.cart;

import com.yearis.e_commerce.entity.Cart;
import com.yearis.e_commerce.payload.cart.CartResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
