package com.yearis.e_commerce.service.product;

import com.yearis.e_commerce.entity.Category;
import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.exception.CategoryNotFoundException;
import com.yearis.e_commerce.exception.ProductNotFoundException;
import com.yearis.e_commerce.payload.request.ProductRequest;
import com.yearis.e_commerce.payload.response.CategoryResponse;
import com.yearis.e_commerce.payload.response.ProductResponse;
import com.yearis.e_commerce.payload.response.ProductResponseSummary;
import com.yearis.e_commerce.repository.CategoryRepository;
import com.yearis.e_commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // --- Mappers ---

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscount(product.getDiscount());
        response.setInventory(product.getInventory());

        // if there is a discount we set the discounted price as price
        if (product.getDiscount() != null && response.getDiscount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal discountAmount = product.getPrice()
                    .multiply(product.getDiscount())
                    .divide(new BigDecimal("100"), RoundingMode.HALF_UP);

            // discounted price = price - discountAmount
            BigDecimal discountedPrice = product.getPrice().subtract(discountAmount);

            response.setDiscountedPrice(discountedPrice);
        } else {

            // in the front-end we can just hide this part if discountedPrice is equal to price
            response.setDiscountedPrice(product.getPrice());
        }

        // now we set categories in response (categories are list in response and set in entity)
        List<CategoryResponse> categoriesSummaries = product.getCategories().stream()
                .map(category -> {
                    CategoryResponse summary = new CategoryResponse();
                    summary.setId(category.getId());
                    summary.setName(category.getName());
                    return summary;
                }).toList();
        response.setCategory(categoriesSummaries);

        return response;
    }

    private ProductResponseSummary mapToSummary(Product product) {

        ProductResponseSummary summary = new ProductResponseSummary();
        summary.setId(product.getId());
        summary.setName(product.getName());
        summary.setBrand(product.getBrand());
        summary.setPrice(product.getPrice());
        summary.setDiscount(product.getDiscount());

        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal discountAmount = product.getPrice()
                    .multiply(product.getDiscount())
                    .divide(new BigDecimal("100"), RoundingMode.HALF_UP);

            // discounted price = price - discountAmount
            BigDecimal discountedPrice = product.getPrice().subtract(discountAmount);

            summary.setDiscountedPrice(discountedPrice);
        } else {

            summary.setDiscountedPrice(product.getPrice());
        }

        return summary;
    }

    private Product mapToEntity(ProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setInventory(request.getInventory());

        // we find the obj of categoryName as those are stored inside product entity
        Set<Category> categories = request.getCategoryIds().stream()
                        .map(id -> categoryRepository.findById(id)
                                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found")))
                                .collect(Collectors.toSet());

        product.setCategories(categories);

        return product;
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest) {

        Product newProduct = mapToEntity(productRequest);

        Product savedProduct = productRepository.save(newProduct);

        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponseSummary> getProductByName(String name, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByNameContaining(name, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategory(String category, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCase(category, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductByBrand(String brand, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByBrand(brand, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByBrandAndName(String brand, String name, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByBrandAndNameContaining(brand, name, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategoryAndName(String category, String name, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCaseAndNameContaining(category, name, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategoryAndBrand(String category, String brand, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCaseAndBrand(category, brand, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getAllProducts(int pageNo, int pageSize) {

        // expensive things at top
        Sort sort = Sort.by("price").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProductById(ProductRequest productRequest, Long productId) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        existingProduct.setName(productRequest.getName());
        existingProduct.setBrand(productRequest.getBrand());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setDiscount(productRequest.getDiscount());
        existingProduct.setInventory(productRequest.getInventory());

        // now we carefully set category
        if (productRequest.getCategoryIds() != null && !productRequest.getCategoryIds().isEmpty()) {

            Set<Category> categories = productRequest.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found")))
                    .collect(Collectors.toSet());

            existingProduct.setCategories(categories);
        }

        Product savedProduct = productRepository.save(existingProduct);

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    @Override
    public Long countProductByBrandAndName(String brand, String name) {

        return productRepository.countByBrandAndNameContaining(brand, name);
    }

    @Override
    public Long countProductByCategoryAndName(String category, String name) {

        return productRepository.countByCategoriesNameIgnoreCaseAndNameContaining(category, name);
    }

    @Override
    public Long countProductByCategoryAndBrand(String category, String brand) {

        return productRepository.countByCategoriesNameIgnoreCaseAndBrand(category, brand);
    }
}
