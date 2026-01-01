package com.yearis.e_commerce.service.auth;

import com.yearis.e_commerce.payload.auth.*;

public interface AuthService {

    String register(RegisterRequest request);

    String verifyEmail(EmailVerificationRequest request);

    String forgotPassword(ForgetPasswordRequest request);

    String verifyOtp(EmailVerificationRequest request);

    String resetPassword(ResetPasswordRequest request);

    String resendOtp(String email);

    JwtAuthResponse login(LoginRequest request);

    JwtAuthResponse refreshToken(RefreshTokenRequest request);

    String logout(RefreshTokenRequest request);
}
