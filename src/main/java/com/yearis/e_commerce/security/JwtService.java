package com.yearis.e_commerce.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    // we extract our accessToken
    String extractUserName(String token);

    // we validate our accessToken against our user details
    boolean isTokenValid(String token, UserDetails userDetails);

    // to generate accessToken
    String generateToken(Map<String, Object> claims, UserDetails userDetails);
}
