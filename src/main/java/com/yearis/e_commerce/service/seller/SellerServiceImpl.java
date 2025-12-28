package com.yearis.e_commerce.service.seller;

import com.yearis.e_commerce.entity.Product;
import com.yearis.e_commerce.entity.Seller;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.enums.ProductStatus;
import com.yearis.e_commerce.enums.SellerStatus;
import com.yearis.e_commerce.exception.ActionNotAllowedException;
import com.yearis.e_commerce.exception.ResourceAlreadyExistsException;
import com.yearis.e_commerce.exception.SellerNotFoundException;
import com.yearis.e_commerce.payload.seller.SellerRequest;
import com.yearis.e_commerce.payload.seller.SellerResponse;
import com.yearis.e_commerce.payload.seller.SellerUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerServiceImpl implements SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    // get our current user
    private User currentUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // --- Mappers ---
    private SellerResponse mapToResponse(Seller seller) {

        SellerResponse response = new SellerResponse();
        response.setStoreName(seller.getStoreName());
        response.setBusinessPhoneNumber(seller.getBusinessPhoneNumber());
        response.setEmail(seller.getUser().getEmail());
        response.setBusinessAddress(seller.getAddress());
        response.setSellerStatus(seller.getSellerStatus());
        response.setRating(seller.getAverageRating());

        return response;
    }

    private Seller mapToEntity(SellerRequest request) {

        Seller seller = new Seller();
        seller.setStoreName(request.getStoreName());
        seller.setBusinessPhoneNumber(request.getBusinessPhoneNumber());
        seller.setAddress(request.getBusinessAddress());
        seller.setSellerStatus(SellerStatus.PENDING);
        seller.setUser(currentUser());

        return seller;
    }

    @Override
    @Transactional
    public SellerResponse applyForSeller(SellerRequest sellerRequest) {

        User currentUser = currentUser();

        Seller seller = mapToEntity(sellerRequest);

        // we check if they already have a store which is not closed
        if (sellerRepository.existsById(currentUser.getId())) {

            if (seller.getSellerStatus() == SellerStatus.PENDING) {

                // means their seller account is not closed so they can't re-apply
                throw new ActionNotAllowedException("Your seller account is under verification by Admin.\nPlease wait for result.");
            }

            if (seller.getSellerStatus() == SellerStatus.APPROVED) {

                // means their seller account is not closed so they can't re-apply
                throw new ResourceAlreadyExistsException("You already have a seller account");
            }

            if (seller.getSellerStatus() == SellerStatus.BANNED) {

                throw new ActionNotAllowedException("Your seller account has been banned due to misconduct.\nContact Admin.");
            }

            // if status is REJECTED or CLOSED then they can re-apply
        }

        // now we check if store name is already taken
        if (sellerRepository.existsByStoreName(seller.getStoreName())) {

            throw new ResourceAlreadyExistsException("Store name '" + sellerRequest.getStoreName() + "' is already taken.");
        }

        // now we check if phone number is already taken
        if (sellerRepository.existsByBusinessPhoneNumber(seller.getBusinessPhoneNumber())) {

            throw new ResourceAlreadyExistsException("Cannot use this phone number.");
        }

        // now we check if address is already taken
        if (sellerRepository.existsByAddress(seller.getAddress())) {

            throw new ResourceAlreadyExistsException("Cannot use this business address");
        }

        Seller savedSeller = sellerRepository.save(seller);

        return mapToResponse(savedSeller);
    }

    @Override
    public SellerResponse getSellerById(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        return mapToResponse(seller);
    }

    @Override
    public List<SellerResponse> getSellerByStoreName(String name, int pageNo, int pageSize) {

        Sort sort = Sort.by("averageRating").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Seller> sellerPage = sellerRepository.findSellersByStoreNameContaining(name, pageable);

        return sellerPage.getContent().stream()
                .map(seller -> mapToResponse(seller))
                .toList();
    }

    @Override
    @Transactional
    public SellerResponse updateSellerInfo(SellerUpdateRequest sellerUpdateRequest) {

        // here we will only update what is sent in the request
        User currentUser = currentUser();

        if (currentUser.getSeller() == null) {
            throw new SellerNotFoundException("Current user does not have a seller account.");
        }

        Seller seller = sellerRepository.findById(currentUser.getSeller().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        boolean isUpdated = false;

        if (sellerUpdateRequest.getStoreName() != null && !sellerUpdateRequest.getStoreName().isBlank()) {

            if (!sellerUpdateRequest.getStoreName().equals(seller.getStoreName())) {
                seller.setStoreName(sellerUpdateRequest.getStoreName());
                isUpdated = true;
            }
        }

        if (sellerUpdateRequest.getBusinessAddress() != null) {

            if (!sellerUpdateRequest.getBusinessAddress().equals(seller.getAddress())) {
                seller.setAddress(sellerUpdateRequest.getBusinessAddress());
                isUpdated = true;
            }
        }

        if (sellerUpdateRequest.getBusinessPhoneNumber() != null && !sellerUpdateRequest.getBusinessPhoneNumber().isBlank()) {

            if (!sellerUpdateRequest.getBusinessPhoneNumber().equals(seller.getBusinessPhoneNumber())) {
                seller.setBusinessPhoneNumber(sellerUpdateRequest.getBusinessPhoneNumber());
                isUpdated = true;
            }
        }

        if (isUpdated) {
            Seller savedSeller = sellerRepository.save(seller);
            return mapToResponse(savedSeller);
        }

        return mapToResponse(seller);
    }

    @Override
    @Transactional
    public void deactivateSellerAccount() {

        User currentUser = currentUser();

        if (currentUser.getSeller() == null) {
            throw new SellerNotFoundException("Current user does not have a seller account.");
        }

        Seller seller = sellerRepository.findById(currentUser.getSeller().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        // 1st we set the seller as CLOSED
        seller.setSellerStatus(SellerStatus.CLOSED);

        // remove the role seller
        currentUser.getRoles().removeIf(role -> role.getName().equals("ROLE_SELLER"));

        // then we find all its products and set them to archived and also set their inventory to 0
        Set<Product> products = seller.getProducts();

        products.forEach(product -> {
            product.setStatus(ProductStatus.ARCHIVED);
            product.setInventory(0);
        });

        productRepository.saveAll(products);

        sellerRepository.save(seller);
    }
}
