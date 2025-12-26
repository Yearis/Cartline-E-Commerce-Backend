package com.yearis.e_commerce.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDeleteRequest {

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 30, message = "Password must be at least 8 characters")
    private String userPassword;

    // here we will also add verification code part too or ig not
}
