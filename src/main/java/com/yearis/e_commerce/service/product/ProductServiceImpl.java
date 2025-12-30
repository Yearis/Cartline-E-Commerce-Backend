package com.yearis.e_commerce.service.product;

import com.yearis.e_commerce.entity.Category;
import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.enums.SellerStatus;
import com.yearis.e_commerce.exception.*;
import com.yearis.e_commerce.payload.product.ProductRequest;
import com.yearis.e_commerce.payload.category.CategoryResponse;
import com.yearis.e_commerce.payload.product.ProductResponse;
import com.yearis.e_commerce.payload.product.ProductResponseSummary;
import com.yearis.e_commerce.payload.seller.SellerInfo;
import com.yearis.e_commerce.repository.category.CategoryRepository;
import com.yearis.e_commerce.repository.product.ProductRepository;
import com.yearis.e_commerce.repository.seller.SellerRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    private User currentUser() {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // --- Mappers ---
    private SellerInfo mapSellerToInfo(Seller seller) {

        SellerInfo info = new SellerInfo();
        info.setId(seller.getId());
        info.setStoreName(seller.getStoreName());

        return info;
    }

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscount(product.getDiscount());
        response.setInventory(product.getInventory());
        response.setAverageRating(product.getAverageRating());
        response.setStatus(product.getStatus());

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
        response.setSeller(mapSellerToInfo(product.getSeller()));

        return response;
    }

    private ProductResponseSummary mapToSummary(Product product) {

        ProductResponseSummary summary = new ProductResponseSummary();
        summary.setId(product.getId());
        summary.setName(product.getName());
        summary.setBrand(product.getBrand());
        summary.setPrice(product.getPrice());
        summary.setDiscount(product.getDiscount());
        summary.setAverageRating(product.getAverageRating());
        summary.setStatus(product.getStatus());

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

        summary.setSeller(mapSellerToInfo(product.getSeller()));

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

        // we map to entity when a product is created so we keep the status as active
        product.setStatus(ProductStatus.ACTIVE);

        // we find the obj of categoryName as those are stored inside product entity
        Set<Category> categories = request.getCategoryIds().stream()
                        .map(id -> categoryRepository.findById(id)
                                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found")))
                                .collect(Collectors.toSet());

        product.setCategories(categories);

        product.setSeller(currentUser().getSeller());

        return product;
    }

    // --- Helper ---
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        int newStock = product.getInventory() - quantity;

        if (newStock < 0) {
            throw new InventoryException("Inventory cannot be less than 0");
        }

        product.setInventory(newStock);
        productRepository.save(product);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        int newStock = product.getInventory() + quantity;

        product.setInventory(newStock);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest) {

        User currentUser = currentUser();

        Seller seller = sellerRepository.findById(currentUser.getSeller().getId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        if (!seller.getSellerStatus().equals(SellerStatus.APPROVED)) {
            throw new ActionNotAllowedException("Your seller account is not approved. You cannot add products.");
        }

        Product newProduct = mapToEntity(productRequest);

        Product savedProduct = productRepository.save(newProduct);

        seller.getProducts().add(savedProduct);

        sellerRepository.save(seller);

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

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByNameContainingAndStatus(name, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategory(String category, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCaseAndStatus(category, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductByBrand(String brand, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByBrandAndStatus(brand, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByBrandAndName(String brand, String name, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByBrandAndNameContainingAndStatus(brand, name, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategoryAndName(String category, String name, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCaseAndNameContainingAndStatus(category, name, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getProductsByCategoryAndBrand(String category, String brand, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findByCategoriesNameIgnoreCaseAndBrandAndStatus(category, brand, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getAllProducts(int pageNo, int pageSize) {

        // expensive things at top
        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findAllByStatus(ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProductById(ProductRequest productRequest, Long productId) {

        User currentUser = currentUser();

        Seller seller = sellerRepository.findById(currentUser.getSeller().getId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        // now we check if the product even belongs to our seller
        Seller owner = existingProduct.getSeller();

        if (!seller.getId().equals(owner.getId())) {

            throw new ResourceAccessDeniedException("You are not the owner");
        }

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

        User currentUser = currentUser();

        Seller seller = sellerRepository.findById(currentUser.getSeller().getId())
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // now we check if the product even belongs to our seller
        Seller owner = product.getSeller();

        if (!seller.getId().equals(owner.getId())) {

            throw new ResourceAccessDeniedException("You are not the owner");
        }

        product.setStatus(ProductStatus.INACTIVE);

        productRepository.save(product);
    }

    @Override
    public Long countProductByBrandAndName(String brand, String name) {

        return productRepository.countByBrandAndNameContainingAndStatus(brand, name, ProductStatus.ACTIVE);
    }

    @Override
    public Long countProductByCategoryAndName(String category, String name) {

        return productRepository.countByCategoriesNameIgnoreCaseAndNameContainingAndStatus(category, name, ProductStatus.ACTIVE);
    }

    @Override
    public Long countProductByCategoryAndBrand(String category, String brand) {

        return productRepository.countByCategoriesNameIgnoreCaseAndBrandAndStatus(category, brand, ProductStatus.ACTIVE);
    }

    @Override
    public List<ProductResponseSummary> getAllProductsBySeller(Long sellerId, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findProductBySellerIdAndStatus(sellerId, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getAllProductBySellerAndCategory(Long sellerId, String category, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findBySellerIdAndCategoriesNameIgnoreCaseAndStatus(sellerId, category, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getAllProductBySellerAndBrand(Long sellerId, String brand, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findBySellerIdAndBrandAndStatus(sellerId, brand, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseSummary> getAllProductBySellerAndName(Long sellerId, String name, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending()
                .and(Sort.by("price")).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productPage = productRepository.findBySellerIdAndNameContainingAndStatus(sellerId, name, ProductStatus.ACTIVE, pageable);

        return productPage.getContent().stream()
                .map(product -> mapToSummary(product))
                .collect(Collectors.toList());
    }
}
