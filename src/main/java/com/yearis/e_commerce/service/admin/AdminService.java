package com.yearis.e_commerce.service.admin;

import com.yearis.e_commerce.enums.SellerStatus;
import com.yearis.e_commerce.payload.seller.SellerResponse;

import java.util.List;

public interface AdminService {

    SellerResponse approveSeller(Long sellerId);

    SellerResponse rejectSeller(Long sellerId);

    SellerResponse banSeller(Long sellerId);

    List<SellerResponse> getSellersByStatus(SellerStatus status, int pageNo, int pageSize);
}
