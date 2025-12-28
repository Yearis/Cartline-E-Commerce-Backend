package com.yearis.e_commerce.payload.product;

import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseSummary {

    private Long id;

    private String name;

    private String brand;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal discountedPrice;

    private ProductStatus status;

    private Seller seller;
}
