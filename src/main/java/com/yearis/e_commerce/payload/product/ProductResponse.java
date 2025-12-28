package com.yearis.e_commerce.payload.product;

import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.payload.category.CategoryResponse;
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

    private ProductStatus status;

    private List<CategoryResponse> category;

    private Seller seller;
}
