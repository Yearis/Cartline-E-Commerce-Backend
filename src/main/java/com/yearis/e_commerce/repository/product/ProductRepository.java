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

    Page<Product> findByCategoriesNameIgnoreCase(String category, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    Page<Product> findByCategoriesNameIgnoreCaseAndBrand(String category, String brand, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    Page<Product> findByBrandAndNameContaining(String brand, String name, Pageable pageable);

    Page<Product> findByCategoriesNameIgnoreCaseAndNameContaining(String category, String name, Pageable pageable);

    Long countByBrandAndNameContaining(String brand, String name);

    Long countByCategoriesNameIgnoreCaseAndNameContaining(String category, String name);

    Long countByCategoriesNameIgnoreCaseAndBrand(String category, String brand);

    List<Product> findByStatus(ProductStatus status);
}

