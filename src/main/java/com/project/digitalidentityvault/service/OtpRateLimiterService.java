package com.project.digitalidentityvault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpRateLimiterService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${otp.rate.limit.duration}")
    private long otpRetryDuration;

    @Value("${otp.max.attempts}")
    private int maxOtpAttempts;

    @Value("${otp.resent.duration}")
    private long otpResetDuration;

    public boolean isOtpRequestAllowed(String email){
        String attemptsKey = "otp_request_attempts:" + email;
        String timestampKey = "otp_request_timestamp:" + email;

        String attemptsStr = (String) redisTemplate.opsForValue().get(attemptsKey);
        String timestampStr = (String) redisTemplate.opsForValue().get(timestampKey);

        Integer attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : null;
        Long lastRequestTimestamp = timestampStr != null ? Long.parseLong(timestampStr) : null;

        // Check if max attempts exceeded
        if (attempts != null && attempts >= maxOtpAttempts) {
            if (has24HoursPassed(lastRequestTimestamp)) {
                redisTemplate.delete(attemptsKey);
            } else {
                return false;
            }
        }
        // Allow request if no timestamp or 60 seconds have passed
        if (lastRequestTimestamp == null || has60SecondsPassed(lastRequestTimestamp)) {
            redisTemplate.opsForValue().set(timestampKey, String.valueOf(System.currentTimeMillis()),
                    otpRetryDuration, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(attemptsKey, String.valueOf(attempts == null ? 1 : attempts + 1),
                    otpRetryDuration, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    private boolean has60SecondsPassed(Long timestamp) {
        long currentTime = System.currentTimeMillis();
        return timestamp == null || (currentTime - timestamp) >= (otpRetryDuration * 1000);
    }

    private boolean has24HoursPassed(Long timestamp) {
        long currentTime = System.currentTimeMillis();
        return timestamp == null || (currentTime - timestamp) >= (otpResetDuration * 1000);
    }
}
