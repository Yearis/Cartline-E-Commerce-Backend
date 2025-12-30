package com.yearis.e_commerce.controller.seller;

import com.yearis.e_commerce.payload.seller.SellerRequest;
import com.yearis.e_commerce.payload.seller.SellerResponse;
import com.yearis.e_commerce.payload.seller.SellerUpdateRequest;
import com.yearis.e_commerce.service.seller.SellerService;
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

@Tag(name = "Seller Rest API Endpoints", description = "Operations for seller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "Apply for Seller Account", description = "User can apply to become a seller")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/apply")
    public ResponseEntity<SellerResponse> applyForSeller(
            @Parameter(description = "Seller application details") @Valid @RequestBody SellerRequest sellerRequest) {

        SellerResponse response = sellerService.applyForSeller(sellerRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Seller by ID", description = "Get details of a seller")
    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerResponse> getSellerById(
            @Parameter(description = "ID of the seller") @PathVariable Long sellerId) {

        SellerResponse response = sellerService.getSellerById(sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Application Status", description = "Check the current status of the logged in user's seller application")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/status")
    public ResponseEntity<String> getSellerApplicationStatus() {

        String statusMessage = sellerService.getSellerApplicationStatus();

        return new ResponseEntity<>(statusMessage, HttpStatus.OK);
    }

    @Operation(summary = "Search Sellers by store name", description = "Search for sellers by store name")
    @GetMapping("/search")
    public ResponseEntity<List<SellerResponse>> getSellerByStoreName(
            @Parameter(description = "Name to search") @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<SellerResponse> response = sellerService.getSellerByStoreName(name, pageNo, pageSize);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Seller Info", description = "Update seller account details")
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/update")
    public ResponseEntity<SellerResponse> updateSellerInfo(
            @Parameter(description = "Details to update") @Valid @RequestBody SellerUpdateRequest request) {

        SellerResponse response = sellerService.updateSellerInfo(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate Seller Account", description = "Close the seller account")
    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivateSellerAccount() {

        sellerService.deactivateSellerAccount();

        return new ResponseEntity<>("Seller account deactivated and products archived successfully.", HttpStatus.OK);
    }
}
