package com.yearis.e_commerce.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

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
    private String getOtp(String email) {
        String key = "otp:" + email;

        return redisTemplate.opsForValue().get(key);
    }

    // delete the otp after we verify its correct so it cant be reused
    public void deleteOtp(String email) {
        String key = "otp:" + email;

        redisTemplate.delete(key);
    }
}
