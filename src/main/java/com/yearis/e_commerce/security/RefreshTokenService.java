package com.yearis.e_commerce.security;

import com.yearis.e_commerce.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisService redisService;

    @Value("${spring.jwt.refresh-token.expiration}")
    private long refreshTokenDuration;

    public String createRefreshToken(String email) {

        String refreshToken = UUID.randomUUID().toString();

        redisService.saveRefreshToken(refreshToken, email, refreshTokenDuration);

        return refreshToken;
    }

    public String verifyRefreshToken(String token) {
        return redisService.getUserEmailFromRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh Token expired or Invalid"));
    }

    public void deleteRefreshToken(String token) {
        redisService.deleteRefreshToken(token);
    }
}
