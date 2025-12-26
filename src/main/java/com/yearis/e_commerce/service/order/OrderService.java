package com.yearis.e_commerce.service.order;

import com.yearis.e_commerce.payload.order.OrderRequest;
import com.yearis.e_commerce.payload.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Long orderId);

     List<OrderResponse> getMyOrder();

    OrderResponse cancelOrder(Long orderId);
     // here we add the inventory back but not in cart
}
