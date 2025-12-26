package com.yearis.e_commerce.controller.category;

import com.yearis.e_commerce.payload.category.CategoryRequest;
import com.yearis.e_commerce.payload.category.CategoryResponse;
import com.yearis.e_commerce.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category Rest API Endpoints", description = "Operations related to categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create a new category", description = "Add a new category to database")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CategoryResponse> addCategory(
            @Parameter(description = "payload to create category") @Valid @RequestBody CategoryRequest request) {

        CategoryResponse savedCategory = categoryService.addCategory(request);

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Get category by id", description = "Find category by id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "ID for category") @PathVariable Long id) {

        CategoryResponse category = categoryService.getCategoryById(id);

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @Operation(summary = "Get category by name", description = "Find category by name")
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> getCategoryByName(
            @Parameter(description = "Name of the category") @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<CategoryResponse> categories = categoryService.getCategoryByName(name, pageNo, pageSize);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @Operation(summary = "Get all categories", description = "Find all categories")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<CategoryResponse> categories = categoryService.getAllCategories(pageNo, pageSize);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @Operation(summary = "Update a category", description = "Update the details of an existing category")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "payload for updated category") @Valid @RequestBody CategoryRequest request,
            @Parameter(description = "ID of the category to update") @PathVariable Long id) {

        CategoryResponse updatedCategory = categoryService.updateCategory(request, id);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @Operation(summary = "Delete a category", description = "Delete an existing category")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(
            @Parameter(description = "ID of category to delete") @PathVariable Long id) {

        categoryService.deleteCategoryById(id);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

}
