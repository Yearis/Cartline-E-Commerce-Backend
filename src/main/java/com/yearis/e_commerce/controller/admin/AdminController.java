package com.yearis.e_commerce.controller.admin;

import com.yearis.e_commerce.enums.SellerStatus;
import com.yearis.e_commerce.payload.seller.SellerResponse;
import com.yearis.e_commerce.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Rest API Endpoints", description = "Operations for admin")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get Sellers by Status", description = "Find sellers by their status")
    @GetMapping("/sellers")
    public ResponseEntity<List<SellerResponse>> getSellersByStatus(
            @Parameter(description = "Status to find seller") @RequestParam SellerStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        List<SellerResponse> responses = adminService.getSellersByStatus(status, pageNo, pageSize);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Approve a Seller", description = "Approve a pending seller application")
    @PutMapping("/sellers/{sellerId}/approve")
    public ResponseEntity<SellerResponse> approveSeller(
            @Parameter(description = "ID of the seller to approve") @PathVariable Long sellerId) {

        SellerResponse response = adminService.approveSeller(sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Reject a Seller", description = "Reject a pending seller application")
    @PutMapping("/sellers/{sellerId}/reject")
    public ResponseEntity<SellerResponse> rejectSeller(
            @Parameter(description = "ID of the seller to reject") @PathVariable Long sellerId) {

        SellerResponse response = adminService.rejectSeller(sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Ban a Seller", description = "Ban an active seller")
    @PutMapping("/sellers/{sellerId}/ban")
    public ResponseEntity<SellerResponse> banSeller(
            @Parameter(description = "ID of the seller to ban") @PathVariable Long sellerId) {

        SellerResponse response = adminService.banSeller(sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
