package com.yearis.e_commerce.payload.cart;

import com.yearis.e_commerce.payload.cartitem.CartItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartResponse {

    private Long id;

    private BigDecimal totalAmount;

    // the items in cart
    private Set<CartItemResponse> items;
}
