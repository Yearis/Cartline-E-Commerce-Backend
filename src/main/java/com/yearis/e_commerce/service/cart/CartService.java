package com.yearis.e_commerce.service.cart;

import com.yearis.e_commerce.payload.cart.CartResponse;
import com.yearis.e_commerce.payload.cartitem.CartItemRequest;

public interface CartService {

    // this will be used for both adding and updating cart
    CartResponse addToCart(CartItemRequest cartItem);

    CartResponse getCart();

    CartResponse decreaseItemQuantity(Long cartItemId);

    CartResponse removeItemFromCart(Long cartItemId);

    CartResponse clearCart();
}
