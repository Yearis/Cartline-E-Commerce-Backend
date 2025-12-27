package com.yearis.e_commerce.payload.order;

import com.yearis.e_commerce.entity.Address;
import lombok.Data;

@Data
public class OrderRequest {

    private Address shippingAddress;
}
