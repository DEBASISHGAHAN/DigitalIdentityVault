package com.project.digitalidentityvault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long sessionTtl;

    public void storeOtp(String email, String otp, Long ttlSeconds){
        redisTemplate.opsForValue().set(email, otp, ttlSeconds , TimeUnit.SECONDS);
    }
    public String getOtp(String email){
        return (String) redisTemplate.opsForValue().get(email);
    }
    public void storeSession(String email, String jwtToken){
        redisTemplate.opsForValue().set(email, jwtToken, sessionTtl, TimeUnit.SECONDS);

    }
    public String getSession(String email){
        return (String) redisTemplate.opsForValue().get(email);
    }
    public void deleteSession(String email){
        redisTemplate.delete(email);
    }
}
