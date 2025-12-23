package com.yearis.e_commerce.payload.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    // we don't need products here as category would be stored in product entity
}
