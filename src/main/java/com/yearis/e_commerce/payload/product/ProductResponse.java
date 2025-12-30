package com.yearis.e_commerce.payload.product;

import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.payload.category.CategoryResponse;
import com.yearis.e_commerce.payload.seller.SellerInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {

    private Long id;

    private String name;

    private String brand;

    private String description;

    private BigDecimal price;

    private BigDecimal discount;

    private BigDecimal discountedPrice;

    private int inventory;

    private Double averageRating;

    private ProductStatus status;

    private List<CategoryResponse> category;

    private SellerInfo seller;
}
