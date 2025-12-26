package com.yearis.e_commerce.service.order;

import com.yearis.e_commerce.entity.*;
import com.yearis.e_commerce.enums.OrderStatus;
import com.yearis.e_commerce.exception.*;
import com.yearis.e_commerce.payload.order.OrderRequest;
import com.yearis.e_commerce.payload.order.OrderResponse;
import com.yearis.e_commerce.payload.orderitem.OrderItemResponse;
import com.yearis.e_commerce.payload.product.ProductResponseSummary;
import com.yearis.e_commerce.repository.cart.CartRepository;

import com.yearis.e_commerce.repository.order.OrderRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import com.yearis.e_commerce.service.cart.CartService;
import com.yearis.e_commerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // --- Mappers ---

    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDateAndTime(order.getOrderDateAndTime());
        response.setOrderStatus(order.getOrderStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setTotalAmount(order.getTotalAmount());

        // now we set the order items
        Set<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> {
                    // we set the response for order items
                    OrderItemResponse orderItemResponse = new OrderItemResponse();
                    orderItemResponse.setId(item.getId());
                    orderItemResponse.setProduct(mapToProductSummary(item.getProduct()));
                    orderItemResponse.setQuantity(item.getQuantity());
                    orderItemResponse.setUnitPrice(item.getUnitPrice());
                    orderItemResponse.setTotalPrice(item.getTotalPrice());
                    return  orderItemResponse;
                }).collect(Collectors.toSet());

        response.setOrderItems(items);

        return response;
    }

    private ProductResponseSummary mapToProductSummary(Product product) {

        ProductResponseSummary summary = new ProductResponseSummary();
        summary.setId(product.getId());
        summary.setName(product.getName());
        summary.setBrand(product.getBrand());
        summary.setDiscount(product.getDiscount());
        summary.setPrice(product.getPrice());

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

    // --- Helpers ---
    private User currentUser() {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        // get current user
        User currentUser = currentUser();

        // to add to a cart we 1st get the cart
        Cart cart = currentUser.getCart();

        if (cart.getCartItems().isEmpty()) {
            throw new ActionNotAllowedException("Cannot place an order with an empty cart.");
        }

        // now we check the inventory
        List<String> outOfStockItemsList = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getInventory() < cartItem.getQuantity())
                .map(cartItem -> String.format("%s (Requested: %d, Available: %d)",
                        cartItem.getProduct().getName(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getInventory()))
                .toList();

        // if the list does contain such elements we will throw an error
        if (!outOfStockItemsList.isEmpty()) {
            String message = "We are unable to fulfill your order due to insufficient inventory for the following items: "
                    + String.join(", ", outOfStockItemsList)
                    + ". Please adjust your cart quantities.";

            throw new InventoryException(message);
        }

        // payment method for future

        // now we make a new order
        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDateAndTime(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setShippingAddress(orderRequest.getShippingAddress());

        order.setOrderItems(cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    // we don't set the id here as its auto-incremented
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setTotalPrice();
                    return orderItem;
                }).collect(Collectors.toSet())
        );

        order.setOrderStatus(OrderStatus.PROCESSING);

        // now we decrease the inventory for each item
        cart.getCartItems().forEach(cartItem ->
                productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity()));

        // and now we save the order
        Order savedOrder = orderRepository.save(order);

        // now we clear user's cart
        cartService.clearCart();

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        User currentUser = currentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        if (!order.getUser().getId().equals(currentUser.getId())) {

            throw new ResourceAccessDeniedException("You cannot view this order.");
        }

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrder() {

        User currentUser = currentUser();

        return currentUser.getOrders().stream()
                .map(order -> mapToResponse(order))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        User currentUser = currentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        if (!order.getUser().getId().equals(currentUser.getId())) {

            throw new ActionNotAllowedException("You cannot cancel this order.");
        }

        // now we check if order status is beyond processing then it's not possible
        if (Set.of(
                    OrderStatus.IN_TRANSIT,
                    OrderStatus.ARRIVED_AT_HUB,
                    OrderStatus.OUT_FOR_DELIVERY,
                    OrderStatus.DELIVERED,
                    OrderStatus.CANCELLED
                ).contains(order.getOrderStatus())) {

            throw new ActionNotAllowedException("Order cannot be cancelled because it is already " + order.getOrderStatus());
        }

        // now we restock
        order.getOrderItems().forEach(orderItem ->
            productService.increaseStock(orderItem.getProduct().getId(), orderItem.getQuantity()));

        order.setOrderStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }
}
