package com.yearis.e_commerce.payload.response;

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

    private List<CategoryResponse> category;
}
