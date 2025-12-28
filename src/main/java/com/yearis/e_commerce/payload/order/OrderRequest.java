package com.yearis.e_commerce.payload.order;

import com.yearis.e_commerce.entity.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    private Address shippingAddress;
}
