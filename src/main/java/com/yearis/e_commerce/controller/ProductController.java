package com.yearis.e_commerce.controller;

import com.yearis.e_commerce.payload.request.ProductRequest;
import com.yearis.e_commerce.payload.response.ProductResponse;
import com.yearis.e_commerce.payload.response.ProductResponseSummary;
import com.yearis.e_commerce.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Rest API Endpoints", description = "Operations related to products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Add a new product", description = "Add a new product to database")
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> addProduct(
            @Parameter(description = "payload for create product") @Valid @RequestBody ProductRequest productRequest) {

        ProductResponse newProduct = productService.addProduct(productRequest);

        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a product by id", description = "To get an product by id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID for product") @PathVariable Long id) {

        ProductResponse product = productService.getProductById(id);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Get products by name", description = "Find products by name")
    @GetMapping("/search/name")
    public ResponseEntity<List<ProductResponseSummary>> getProductByName(
            @Parameter(description = "Name of product") @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductByName(name, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by category", description = "Find products by category")
    @GetMapping("/search/category")
    public ResponseEntity<List<ProductResponseSummary>> getProductsByCategory(
            @Parameter(description = "Category of product") @RequestParam String category,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductsByCategory(category, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by brand", description = "Find products by brand")
    @GetMapping("/search/brand")
    public ResponseEntity<List<ProductResponseSummary>> getProductByBrand(
            @Parameter(description = "Brand of product") @RequestParam String brand,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductByBrand(brand, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by name and brand", description = "Find products by name and brand")
    @GetMapping("/search/name-and-brand")
    public ResponseEntity<List<ProductResponseSummary>> getProductsByBrandAndName(
            @Parameter(description = "Brand of product") @RequestParam String brand,
            @Parameter(description = "Name of product") @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductsByBrandAndName(brand, name, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by category and name", description = "Find products by category and name")
    @GetMapping("/search/category-and-name")
    public ResponseEntity<List<ProductResponseSummary>> getProductsByCategoryAndName(
            @Parameter(description = "Category of product") @RequestParam String category,
            @Parameter(description = "Name of product") @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductsByCategoryAndName(category, name, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by category and brand", description = "Find products by category and brand")
    @GetMapping("/search/category-and-brand")
    public ResponseEntity<List<ProductResponseSummary>> getProductsByCategoryAndBrand(
            @Parameter(description = "Category of product") @RequestParam String category,
            @Parameter(description = "Brand of product") @RequestParam String brand,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getProductsByCategoryAndBrand(category, brand, pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get all products", description = "Find all products")
    @GetMapping
    public ResponseEntity<List<ProductResponseSummary>> getAllProducts(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<ProductResponseSummary> products = productService.getAllProducts(pageNo, pageSize);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Update a product", description = "Update existing product in database")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductById(
            @Parameter(description = "payload for updated product") @Valid @RequestBody ProductRequest productRequest,
            @Parameter(description = "ID of the product to update") @PathVariable Long id) {

        ProductResponse updatedProduct = productService.updateProductById(productRequest, id);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @Operation(summary = "Delete a product", description = "Delete an existing product")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(
            @Parameter(description = "ID of product to delete") @PathVariable Long id) {

        productService.deleteProductById(id);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
