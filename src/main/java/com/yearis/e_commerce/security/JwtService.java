package com.yearis.e_commerce.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    // we extract our token
    String extractUserName(String token);

    // we validate our token against our user details
    boolean isTokenValid(String token, UserDetails userDetails);

    // to generate token
    String generateToken(Map<String, Object> claims, UserDetails userDetails);
}
