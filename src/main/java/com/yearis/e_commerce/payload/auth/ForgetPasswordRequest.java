package com.yearis.e_commerce.payload.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ForgetPasswordRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    String email;
}
