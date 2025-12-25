package com.yearis.e_commerce.payload.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class JwtAuthResponse {

    @Setter
    private String token;

    private final String tokenType = "Bearer";

    public JwtAuthResponse(String token) {
        this.token = token;
    }

}
