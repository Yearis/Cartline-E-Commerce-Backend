package com.yearis.e_commerce.payload.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtAuthResponse {
    private String accessToken;
    private String refreshToken;

    private final String tokenType = "Bearer";

    public JwtAuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
