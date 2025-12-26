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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart Rest API Endpoints", description = "Operations related to cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add items to the cart", description = "Adding products to the cart")
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Parameter(description = "payload for add item") @Valid @RequestBody CartItemRequest cartItem) {

        CartResponse response = cartService.addToCart(cartItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get the items of cart", description = "Get all items of cart and their total amount")
    @GetMapping("/items")
    public ResponseEntity<CartResponse> getCart() {

        CartResponse response = cartService.getCart();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Decrease the quantity", description = "Decrease the quantity of item in cart")
    @PutMapping("/items/{cartItemId}/decrease")
    public ResponseEntity<CartResponse> decreaseItemQuantity(
            @Parameter(description = "ID for the cart item") @PathVariable Long cartItemId) {

        CartResponse response = cartService.decreaseItemQuantity(cartItemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete item from cart", description = "Delete all the quantities of an item from cart")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @Parameter(description = "ID for the cart item") @PathVariable Long cartItemId) {

        CartResponse response = cartService.removeItemFromCart(cartItemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Empty the cart", description = "Delete all the existing items from the cart")
    @DeleteMapping()
    public ResponseEntity<CartResponse> clearCart() {

        CartResponse response = cartService.clearCart();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
