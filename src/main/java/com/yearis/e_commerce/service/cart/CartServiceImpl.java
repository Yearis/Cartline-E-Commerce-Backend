package com.yearis.e_commerce.service.cart;

import com.yearis.e_commerce.entity.Cart;
import com.yearis.e_commerce.entity.CartItem;
import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.exception.CartItemNotFoundException;
import com.yearis.e_commerce.exception.CartNotFoundException;
import com.yearis.e_commerce.exception.ProductNotFoundException;
import com.yearis.e_commerce.payload.cart.CartResponse;
import com.yearis.e_commerce.payload.cartitem.CartItemRequest;
import com.yearis.e_commerce.payload.cartitem.CartItemResponse;
import com.yearis.e_commerce.payload.product.ProductResponseSummary;
import com.yearis.e_commerce.repository.cart.CartRepository;
import com.yearis.e_commerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    // --- Mappers ---

    private CartResponse mapToResponse(Cart cart) {

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());

        // we need to add items to cart
        Set<CartItemResponse> items = cart.getCartItems().stream()
                .map(item -> {
                    // we set the item to response
                    CartItemResponse cartItemResponse = new CartItemResponse();
                    cartItemResponse.setId(item.getId());
                    cartItemResponse.setProduct(mapToProductSummary(item.getProduct()));
                    cartItemResponse.setQuantity(item.getQuantity());
                    cartItemResponse.setUnitPrice(item.getUnitPrice());
                    cartItemResponse.setTotalPrice(item.getTotalPrice());
                    return cartItemResponse;
                }).collect(Collectors.toSet());

        response.setItems(items);

        return response;
    }

    // copied from our ProductServiceImpl
    private ProductResponseSummary mapToProductSummary(Product product) {

        ProductResponseSummary summary = new ProductResponseSummary();
        summary.setId(product.getId());
        summary.setName(product.getName());
        summary.setBrand(product.getBrand());
        summary.setPrice(product.getPrice());
        summary.setDiscount(product.getDiscount());

        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal discountAmount = product.getPrice()
                    .multiply(product.getDiscount())
                    .divide(new BigDecimal("100"), RoundingMode.HALF_UP);

            // discounted price = price - discountAmount
            BigDecimal discountedPrice = product.getPrice().subtract(discountAmount);

            summary.setDiscountedPrice(discountedPrice);
        } else {

            summary.setDiscountedPrice(product.getPrice());
        }

        return summary;
    }

    // --- Helper ---
    private void calculateCartTotal(Cart cart) {
        // no we recalculate the total price of the cart
        BigDecimal totalCartAmount = cart.getCartItems().stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalCartAmount);
    }

    private BigDecimal calculateDiscountedPrice(Product product) {

        // we take the product's discount and calculate the unit price for 1
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal discountAmount = product.getPrice()
                    .multiply(product.getDiscount())
                    .divide(new BigDecimal("100"), RoundingMode.HALF_UP);

            return product.getPrice().subtract(discountAmount);
        }
        // or else it's the original price
        return product.getPrice();
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long cartId, CartItemRequest cartItem) {

        // to add to a cart we 1st get the cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart with ID " + cartId + " not found"));

        // we need to check if the product to be added exists or not
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + cartItem.getProductId() + " not found"));

        // now we check if the product's inventory has sufficient stock or not
        if (product.getInventory() < cartItem.getQuantity()) {

            throw new RuntimeException("Not enough inventory!!\nAvailable Stock: " + product.getInventory());
        }

        // now we check if the product is already in the cart or not
        // if it is we just increase the quantity in cart
        // we use optional as we don't know if it exists or not
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findAny();

        // now if existing item is there we simply update its quantity else we add it
        if (existingItem.isPresent()) {

            // we add the current quantity to new quantity
            int newQuantity = existingItem.get().getQuantity() + cartItem.getQuantity();

            // now we check if it doesn't exceed inventory
            if (newQuantity > product.getInventory()) {

                throw new RuntimeException("Cant add more!!\nAvailable Stock: " + product.getInventory());
            }

            existingItem.get().setQuantity(newQuantity);
            existingItem.get().setTotalPrice();
        } else {
            // if it's not present we add it to cart
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setCart(cart);
            newItem.setQuantity(cartItem.getQuantity());
            newItem.setUnitPrice(calculateDiscountedPrice(product));
            newItem.setTotalPrice();

            cart.getCartItems().add(newItem);
        }

        // now we update the cart total
        calculateCartTotal(cart);

        // now we save
        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long id) {

        // 1st we check if cart exists or not
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart with ID " + id + " not found"));

        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse decreaseItemQuantity(Long cartId, Long cartItemId) {

        // 1st we check if cart exists or not
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart with ID " + cartId + " not found"));

        // then we check if the item we want to remove is present in the cart or not
        CartItem existingItem = cart.getCartItems().stream()
                .filter(items -> items.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Item with ID " + cartItemId + " not found in cart"));

        if (existingItem.getQuantity() <= 1) {
            removeItemFromCart(cartId, cartItemId);
        }

        int newQuantity = existingItem.getQuantity() - 1;
        existingItem.setQuantity(newQuantity);

        // now we calculate new total of item
        existingItem.setTotalPrice();

        // now we set new cart total
        calculateCartTotal(cart);

        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartId, Long cartItemId) {

        // 1st we check if cart exists or not
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart with ID " + cartId + " not found"));

        // then we check if the item we want to remove is present in the cart or not
        CartItem existingItem = cart.getCartItems().stream()
                .filter(items -> items.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Item with ID " + cartItemId + " not found in cart"));

        // we removed it
        cart.getCartItems().remove(existingItem);

        // now we fix
        calculateCartTotal(cart);

        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse clearCart(Long id) {

        // 1st we check if cart exists or not
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart with ID " + id + " not found"));

        // we remove everything from list
        cart.getCartItems().clear();

        // we set the totalAmount to 0
        cart.setTotalAmount(BigDecimal.ZERO);

        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }
}
