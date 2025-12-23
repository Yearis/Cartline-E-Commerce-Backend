package com.yearis.e_commerce.service.cart;

import com.yearis.e_commerce.payload.cart.CartResponse;
import com.yearis.e_commerce.payload.cartitem.CartItemRequest;

public interface CartService {

    // this will be used for both adding and updating cart
    CartResponse addToCart(Long cartId, CartItemRequest cartItem);

    CartResponse getCart(Long id);

    CartResponse decreaseItemQuantity(Long cartId, Long cartItemId);

    CartResponse removeItemFromCart(Long cartId, Long cartItemId);

    CartResponse clearCart(Long id);
}
