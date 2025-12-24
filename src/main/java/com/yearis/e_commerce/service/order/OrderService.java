package com.yearis.e_commerce.service.order;

import com.yearis.e_commerce.payload.order.OrderRequest;
import com.yearis.e_commerce.payload.order.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Long orderId);

    // List<OrderResponse> getOrderHistoryForUser(Long userId);

    OrderResponse cancelOrder(Long orderId);
     // here we add the inventory back but not in cart
}
