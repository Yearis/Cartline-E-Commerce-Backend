package com.yearis.e_commerce.repository.category;

import com.yearis.e_commerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByNameContainingIgnoreCase(String categoryName, Pageable pageable);

    boolean existsByName(String name);
}

