package com.yearis.e_commerce.payload.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
