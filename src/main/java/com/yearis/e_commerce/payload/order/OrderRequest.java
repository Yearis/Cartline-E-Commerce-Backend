package com.yearis.e_commerce.payload.order;

import lombok.Data;

@Data
public class OrderRequest {

    private Long cartId;

    private String shippingAddress;
}
