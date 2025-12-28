package com.yearis.e_commerce.repository.product;

import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoriesNameIgnoreCaseAndStatus(String category, ProductStatus status, Pageable pageable);

    Page<Product> findByBrandAndStatus(String brand, ProductStatus status, Pageable pageable);

    Page<Product> findByCategoriesNameIgnoreCaseAndBrandAndStatus(String category, String brand, ProductStatus status, Pageable pageable);

    Page<Product> findByNameContainingAndStatus(String name, ProductStatus status, Pageable pageable);

    Page<Product> findByBrandAndNameContainingAndStatus(String brand, String name, ProductStatus status, Pageable pageable);

    Page<Product> findByCategoriesNameIgnoreCaseAndNameContainingAndStatus(String category, String name, ProductStatus status, Pageable pageable);

    Long countByBrandAndNameContainingAndStatus(String brand, String name, ProductStatus status);

    Long countByCategoriesNameIgnoreCaseAndNameContainingAndStatus(String categories_name, String name, ProductStatus status);

    Long countByCategoriesNameIgnoreCaseAndBrandAndStatus(String categories_name, String brand, ProductStatus status);

    List<Product> findByStatus(ProductStatus status);

    Page<Product> findProductBySellerIdAndStatus(Long sellerId, ProductStatus status, Pageable pageable);

    Page<Product> findAllByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findBySellerIdAndCategoriesNameIgnoreCaseAndStatus(Long sellerId, String category, ProductStatus status, Pageable pageable);

    Page<Product> findBySellerIdAndBrandAndStatus(Long sellerId, String brand, ProductStatus status, Pageable pageable);

    Page<Product> findBySellerIdAndNameContainingAndStatus(Long sellerId, String name, ProductStatus status, Pageable pageable);
}

