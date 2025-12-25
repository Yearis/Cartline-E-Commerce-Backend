package com.yearis.e_commerce.service.auth;

import com.yearis.e_commerce.payload.auth.JwtAuthResponse;
import com.yearis.e_commerce.payload.auth.LoginRequest;
import com.yearis.e_commerce.payload.auth.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    JwtAuthResponse login(LoginRequest request);
}
