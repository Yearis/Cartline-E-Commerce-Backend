package com.yearis.e_commerce.service.seller;


import com.yearis.e_commerce.payload.seller.SellerRequest;
import com.yearis.e_commerce.payload.seller.SellerResponse;
import com.yearis.e_commerce.payload.seller.SellerUpdateRequest;

import java.util.List;

public interface SellerService {

    SellerResponse applyForSeller(SellerRequest sellerRequest);

    SellerResponse getSellerById(Long sellerId);

    String getSellerApplicationStatus();

    List<SellerResponse> getSellerByStoreName(String name, int pageNo, int pageSize);

    SellerResponse updateSellerInfo(SellerUpdateRequest sellerUpdateRequest);

    void deactivateSellerAccount();
}
