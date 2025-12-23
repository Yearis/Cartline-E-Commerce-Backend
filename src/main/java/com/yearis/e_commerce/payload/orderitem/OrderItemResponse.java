package com.yearis.e_commerce.payload.orderitem;

import com.yearis.e_commerce.payload.product.ProductResponseSummary;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Long id;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private ProductResponseSummary product;
}
