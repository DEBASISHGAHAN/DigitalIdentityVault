package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.util.Constants;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long sessionTtl;

    @Value("${spring.data.redis.timeout}")
    private long otpTtl;

    public void storeOtp(String email, String otp) {
        redisTemplate.opsForValue().set(email, otp, otpTtl, TimeUnit.SECONDS);
    }

    public String getOtp(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }

    public void storeSession(String email, String jwtToken) {
        redisTemplate.opsForValue().set(email, jwtToken, sessionTtl, TimeUnit.SECONDS);
    }

    public String getSession(String email) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(email))
                .orElseThrow(() -> new JwtException(Constants.INVALID_SESSION));
    }

    public void deleteSession(String email) {
        redisTemplate.delete(email);
    }
}