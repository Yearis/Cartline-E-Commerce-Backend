package com.yearis.e_commerce.service.admin;

import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.entity.Role;
import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.enums.SellerStatus;
import com.yearis.e_commerce.exception.ActionNotAllowedException;
import com.yearis.e_commerce.exception.RoleNotFoundException;
import com.yearis.e_commerce.exception.SellerNotFoundException;
import com.yearis.e_commerce.payload.seller.SellerResponse;
import com.yearis.e_commerce.repository.product.ProductRepository;
import com.yearis.e_commerce.repository.role.RoleRepository;
import com.yearis.e_commerce.repository.seller.SellerRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;

    // --- Mappers ---
    private SellerResponse mapToResponse(Seller seller) {

        SellerResponse response = new SellerResponse();
        response.setId(seller.getId());
        response.setStoreName(seller.getStoreName());
        response.setBusinessPhoneNumber(seller.getBusinessPhoneNumber());
        response.setEmail(seller.getUser().getEmail());
        response.setBusinessAddress(seller.getAddress());
        response.setSellerStatus(seller.getSellerStatus());
        response.setRating(seller.getAverageRating());

        return response;
    }

    @Override
    @Transactional
    public SellerResponse approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));

        if (seller.getSellerStatus() == SellerStatus.APPROVED) {
            throw new ActionNotAllowedException("Seller is already approved");
        }

        seller.setSellerStatus(SellerStatus.APPROVED);

        User user = seller.getUser();
        Role sellerRole = roleRepository.findByName("ROLE_SELLER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        user.getRoles().add(sellerRole);

        userRepository.save(user);

        return mapToResponse(sellerRepository.save(seller));
    }

    @Override
    @Transactional
    public SellerResponse rejectSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));

        seller.setSellerStatus(SellerStatus.REJECTED);

        // as seller is not approved it doesn't have the role seller,
        // but we will make a check regardless
        seller.getUser().getRoles().removeIf(role -> role.getName().equals("ROLE_SELLER"));

        userRepository.save(seller.getUser());

        return mapToResponse(sellerRepository.save(seller));
    }

    @Override
    @Transactional
    public SellerResponse banSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));

        seller.setSellerStatus(SellerStatus.BANNED);

        seller.getUser().getRoles().removeIf(role -> role.getName().equals("ROLE_SELLER"));
        userRepository.save(seller.getUser());

        // then we find all its products and set them to archived and also set their inventory to 0
        Set<Product> products = seller.getProducts();

        products.forEach(product -> {
            product.setStatus(ProductStatus.ARCHIVED);
            product.setInventory(0);
        });

        productRepository.saveAll(products);

        return mapToResponse(sellerRepository.save(seller));
    }

    // for approving or rejecting users
    @Override
    public List<SellerResponse> getSellersByStatus(SellerStatus status, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // This works for PENDING, APPROVED, BANNED, REJECTED - all in one!
        Page<Seller> sellerPage = sellerRepository.findSellersBySellerStatus(status, pageable);

        return sellerPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }
}
