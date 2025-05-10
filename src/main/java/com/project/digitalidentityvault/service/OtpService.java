package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisService redisService;

    public String generateOtp(String email){
        String otp = OtpGenerator.generateOtp();
        redisService.storeOtp(email, otp);
        return otp;
    }
    public boolean validateOtp(String email, String otp){
        String storedOtp = redisService.getOtp(email);
        return otp != null && otp.equals(storedOtp);
    }
}
