package com.yearis.e_commerce.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // we save otp for 5 mins
    public void saveOtp(String email, String otp) {
        String key = "otp:" + email;

        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5));
    }

    // get otp (for checking)
    public String getOtp(String email) {
        String key = "otp:" + email;

        return redisTemplate.opsForValue().get(key);
    }

    // delete the otp after we verify its correct so it cant be reused
    public void deleteOtp(String email) {
        String key = "otp:" + email;

        redisTemplate.delete(key);
    }

    public boolean isBlocked(String email) {
        String attempts = redisTemplate.opsForValue().get("attempts:" + email);

        return "BLOCKED".equals(attempts);
    }

    public void incrementAttemptsFailed(String email) {
        String key = "attempts:" + email;
        String currentAttempts = redisTemplate.opsForValue().get(key);

        if ("BLOCKED".equals(currentAttempts)) {
            return;
        }

        int attempts = 0;
        if (currentAttempts != null) {
            try {
                attempts = Integer.parseInt(currentAttempts);
            } catch (NumberFormatException e) {
                attempts = 0;
            }
        }
        attempts++;

        if (attempts >= 5) {
            redisTemplate.opsForValue().set(key, "BLOCKED", Duration.ofMinutes(15));
        } else {
            redisTemplate.opsForValue().set(key, String.valueOf(attempts), Duration.ofMinutes(5));
        }
    }

    public void clearAttempts(String email) {
        redisTemplate.delete("attempts:" + email);
    }

    public void saveRefreshToken(String token, String email, long duration) {
        redisTemplate.opsForValue().set("refresh_token:" + token, email, Duration.ofSeconds(duration));
    }

    public Optional<String> getUserEmailFromRefreshToken(String token) {
        String email = redisTemplate.opsForValue().get("refresh_token:" + token);
        return Optional.ofNullable(email);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete("refresh_token:" + token);
    }
}
