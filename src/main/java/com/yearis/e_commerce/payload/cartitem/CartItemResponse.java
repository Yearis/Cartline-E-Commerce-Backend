package com.yearis.e_commerce.payload.cartitem;

import com.yearis.e_commerce.payload.product.ProductResponseSummary;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {

    private Long id;
    
    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private ProductResponseSummary product;
}
