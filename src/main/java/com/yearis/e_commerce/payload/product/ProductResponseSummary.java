package com.yearis.e_commerce.payload.product;

import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.payload.seller.SellerInfo;
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

    private Double averageRating;

    private ProductStatus status;

    private SellerInfo seller;
}
