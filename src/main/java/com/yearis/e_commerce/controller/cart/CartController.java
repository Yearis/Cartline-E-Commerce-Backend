package com.yearis.e_commerce.controller.cart;

import com.yearis.e_commerce.payload.cart.CartResponse;
import com.yearis.e_commerce.payload.cartitem.CartItemRequest;
import com.yearis.e_commerce.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart Rest API Endpoints", description = "Operations related to cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add items to the cart", description = "Adding products to the cart")
    @PostMapping("/{cartId}/add")
    public ResponseEntity<CartResponse> addToCart(
            @Parameter(description = "ID for the cart") @PathVariable Long cartId,
            @Parameter(description = "payload for add item") @Valid @RequestBody CartItemRequest cartItem) {

        CartResponse response = cartService.addToCart(cartId, cartItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get the items of cart", description = "Get all items of cart and their total amount")
    @GetMapping("/{id}/items")
    public ResponseEntity<CartResponse> getCart(
            @Parameter(description = "ID for the cart") @PathVariable Long id) {

        CartResponse response = cartService.getCart(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Decrease the quantity", description = "Decrease the quantity of item in cart")
    @PutMapping("/{cartId}/items/{cartItemId}/decrease")
    public ResponseEntity<CartResponse> decreaseItemQuantity(
            @Parameter(description = "ID for the cart") @PathVariable Long cartId,
            @Parameter(description = "ID for the cart item") @PathVariable Long cartItemId) {

        CartResponse response = cartService.decreaseItemQuantity(cartId, cartItemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete item from cart", description = "Delete all the quantities of an item from cart")
    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @Parameter(description = "ID for the cart") @PathVariable Long cartId,
            @Parameter(description = "ID for the cart item") @PathVariable Long cartItemId) {

        CartResponse response = cartService.removeItemFromCart(cartId, cartItemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Empty the cart", description = "Delete all the existing items from the cart")
    @DeleteMapping("/{id}")
    public ResponseEntity<CartResponse> clearCart(
            @Parameter(description = "ID for the cart") @PathVariable Long id) {

        CartResponse response = cartService.clearCart(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
