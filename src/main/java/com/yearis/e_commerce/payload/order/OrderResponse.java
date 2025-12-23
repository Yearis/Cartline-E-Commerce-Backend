package com.yearis.e_commerce.payload.order;

import com.yearis.e_commerce.enums.OrderStatus;
import com.yearis.e_commerce.payload.orderitem.OrderItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class OrderResponse {

    private Long id;

    private LocalDate orderDate;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private Set<OrderItemResponse> orderItems;
}
