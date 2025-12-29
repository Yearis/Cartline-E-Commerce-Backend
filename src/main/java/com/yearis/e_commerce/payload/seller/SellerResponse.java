package com.yearis.e_commerce.payload.seller;

import com.yearis.e_commerce.entity.Address;
import com.yearis.e_commerce.enums.SellerStatus;
import lombok.Data;

@Data
public class SellerResponse {

    private Long id;

    private String storeName;

    private SellerStatus sellerStatus;

    private String businessPhoneNumber;

    private String email;

    private Address businessAddress;

    private Double rating;

    // below we can put products but those are lazy so will require a new api endpoint for it
}
