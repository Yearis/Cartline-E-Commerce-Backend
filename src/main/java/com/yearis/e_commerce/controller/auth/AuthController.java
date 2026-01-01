package com.yearis.e_commerce.controller.auth;

import com.yearis.e_commerce.payload.auth.*;
import com.yearis.e_commerce.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth Rest API Endpoints", description = "Operations related to authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a user", description = "Register/create a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "payload for registering") @Valid @RequestBody RegisterRequest request) {

        String response = authService.register(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Verify user", description = "Verify a user")
    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @Parameter(description = "Verification Payload") @Valid @RequestBody EmailVerificationRequest request) {

        String response = authService.verifyEmail(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Forgot Password", description = "Send OTP to reset password")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Parameter(description = "Payload with email") @Valid @RequestBody ForgetPasswordRequest request) {

        String response = authService.forgotPassword(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Verify OTP", description = "Check if OTP is valid for password reset")
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @Parameter(description = "Payload with Email and OTP") @Valid @RequestBody EmailVerificationRequest request) {

        String response = authService.verifyOtp(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Reset Password", description = "Set new password using OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Payload with OTP and New Passwords") @Valid @RequestBody ResetPasswordRequest request) {

        String response = authService.resetPassword(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Resend OTP", description = "Resend OTP for verification")
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @Parameter(description = "User's email") @RequestParam String email) {

        String response = authService.resendOtp(email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Login a user", description = "Login an existing user")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(
            @Parameter(description = "payload for logging in") @Valid @RequestBody LoginRequest request) {

        JwtAuthResponse response = authService.login(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Refresh Token", description = "Get a new access token using ur refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(
            @Parameter(description = "Refresh Token Payload") @RequestBody RefreshTokenRequest request) {

        JwtAuthResponse response = authService.refreshToken(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Logout user", description = "Delete the refresh token so the user gets logged out")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(description = "Refresh Token to delete") @RequestBody RefreshTokenRequest request) {

        String response = authService.logout(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
