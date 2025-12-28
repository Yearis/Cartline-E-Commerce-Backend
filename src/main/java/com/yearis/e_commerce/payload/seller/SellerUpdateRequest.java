package com.yearis.e_commerce.payload.seller;

import com.yearis.e_commerce.entity.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerUpdateRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 4, max = 250, message = "Store name must be at least 4 characters")
    private String storeName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must be between 10 and 12 digits")
    private String businessPhoneNumber;

    @NotNull(message = "Business address is required")
    @Valid
    private Address businessAddress;
}
