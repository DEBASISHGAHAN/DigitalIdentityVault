package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisService redisService;

    @Value("${spring.data.redis.timeout}")
    private long ttl;

    public String generateOtp(String email){
        String otp = OtpGenerator.generateOtp();
        redisService.storeOtp(email, otp, ttl);
        return otp;
    }
    public boolean validateOtp(String email, String otp){
        String storedOtp = redisService.getOtp(email);
        return otp != null && otp.equals(storedOtp);
    }
}
