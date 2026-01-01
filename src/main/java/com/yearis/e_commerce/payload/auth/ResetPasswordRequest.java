package com.yearis.e_commerce.payload.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "OTP is mandatory")
    String otp;

    @NotBlank(message = "This field cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "This field cannot be blank")
    private String confirmationNewPassword;
}
