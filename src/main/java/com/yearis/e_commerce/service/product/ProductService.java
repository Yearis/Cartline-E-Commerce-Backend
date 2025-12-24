package com.yearis.e_commerce.service.product;

import com.yearis.e_commerce.payload.product.ProductRequest;
import com.yearis.e_commerce.payload.product.ProductResponse;
import com.yearis.e_commerce.payload.product.ProductResponseSummary;

import java.util.List;

public interface ProductService {

    void increaseStock(Long productId, int quantity);

    void reduceStock(Long productId, int quantity);

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long id);

    List<ProductResponseSummary> getProductsByCategory(String category, int pageNo, int pageSize);

    List<ProductResponseSummary> getProductByBrand(String brand, int pageNo, int pageSize);

    List<ProductResponseSummary> getProductByName(String name, int pageNo, int pageSize);

    List<ProductResponseSummary> getProductsByBrandAndName(String brand, String name, int pageNo, int pageSize);

    List<ProductResponseSummary> getProductsByCategoryAndName(String category, String name, int pageNo, int pageSize);

    List<ProductResponseSummary> getProductsByCategoryAndBrand(String category, String brand, int pageNo, int pageSize);

    List<ProductResponseSummary> getAllProducts(int pageNo, int pageSize);

    ProductResponse updateProductById(ProductRequest productRequest, Long productId);

    void deleteProductById(Long id);

    Long countProductByBrandAndName(String brand, String name);

    Long countProductByCategoryAndName(String category, String name);

    Long countProductByCategoryAndBrand(String category, String brand);
}
