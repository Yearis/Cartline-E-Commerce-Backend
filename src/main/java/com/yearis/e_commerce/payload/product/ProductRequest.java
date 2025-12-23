package com.yearis.e_commerce.payload.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @DecimalMax(value = "90.0", message = "Discount cannot exceed 90%")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @Min(value = 0, message = "Inventory cannot be negative")
    private int inventory;

    @NotNull(message = "At least one category is required")
    private Set<Long> categoryIds;
}
