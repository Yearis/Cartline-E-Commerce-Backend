package com.yearis.e_commerce.service.category;

import com.yearis.e_commerce.payload.category.CategoryRequest;
import com.yearis.e_commerce.payload.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse addCategory(CategoryRequest request);

    CategoryResponse getCategoryById(Long categoryId);

    List<CategoryResponse> getCategoryByName(String name, int pageNo, int pageSize);

    List<CategoryResponse> getAllCategories(int pageNo, int pageSize);

    CategoryResponse updateCategory(CategoryRequest request, Long categoryId);

    void deleteCategoryById(Long id);
}

