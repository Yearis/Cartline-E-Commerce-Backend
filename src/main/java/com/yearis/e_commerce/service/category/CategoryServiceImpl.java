package com.yearis.e_commerce.service.category;

import com.yearis.e_commerce.entity.Category;
import com.yearis.e_commerce.exception.CategoryNotFoundException;
import com.yearis.e_commerce.payload.request.CategoryRequest;
import com.yearis.e_commerce.payload.response.CategoryResponse;
import com.yearis.e_commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // --- Mappers ---

    private CategoryResponse mapToResponse(Category category) {

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());

        return response;
    }

    private Category mapToEntity(CategoryRequest request) {

        Category category = new Category();
        category.setName(request.getName());

        return category;
    }

    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryNotFoundException("Category with name '" + request.getName() + "' already exists.");
        }

        Category newCategory = mapToEntity(request);

        Category savedCategory = categoryRepository.save(newCategory);

        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getCategoryByName(String name, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, pageable);

        return categoryPage.getContent().stream()
                .map(category -> mapToResponse(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getAllCategories(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.getContent().stream()
                .map(category -> mapToResponse(category))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest request, Long categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        existingCategory.setName(request.getName());

        Category savedCategory = categoryRepository.save(existingCategory);

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        categoryRepository.delete(existingCategory);
    }
}
