package com.yearis.e_commerce.payload.order;

import com.yearis.e_commerce.entity.Address;
import com.yearis.e_commerce.enums.OrderStatus;
import com.yearis.e_commerce.payload.orderitem.OrderItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderResponse {

    private Long id;

    private LocalDateTime orderDateAndTime;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private Address shippingAddress;

    private Set<OrderItemResponse> orderItems;
}
