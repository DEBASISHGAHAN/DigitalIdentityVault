package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.dto.UserDto;
import com.project.digitalidentityvault.entity.User;
import com.project.digitalidentityvault.exception.*;
import com.project.digitalidentityvault.repository.UserRepository;
import com.project.digitalidentityvault.util.Constants;
import com.project.digitalidentityvault.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.project.digitalidentityvault.util.Validation.validateEmail;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final MailService mailService;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final OtpRateLimiterService otpRateLimiterService;

    public String sendOtpToNewUser(UserDto request) throws UserAlreadyExistsException, UserException{
        log.info("Registering user with email: {}", request.getEmail());
        validateEmail(request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyExistsException(Constants.USER_EXISTED);
        }

        if (request.getPassword().length() < 8){
            throw new UserException(Constants.INVALID_PASSWORD);
        }

        if (!otpRateLimiterService.isOtpRequestAllowed(request.getEmail())) {
            throw new UserException(Constants.OTP_REQUEST_EXCEED);
        }
        // Generate OTP
        String otp = otpService.generateOtp(request.getEmail());
        log.info("Generated OTP for {}: {}", request.getEmail(), otp);
        // Send OTP via Email
        mailService.sendOtpMail(request.getEmail(), "OTP Verification", otp);

        return Constants.SEND_OTP;
    }

    public String verifyOtp(UserDto request) throws UserException, UsernameNotFoundException{
        log.info("Verifying OTP for email: {}", request.getEmail());
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())){
            throw new UserException(Constants.INVALID_OTP);
        }
            // Create and Save User
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .verified(true)
                    .lastActiveAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            redisService.deleteOtp(request.getEmail());
            // Generate JWT Token
            String token = jwtUtil.generateToken(user.getEmail());
            log.info("JWT generated for {}: " ,token);
            redisService.storeSession(user.getEmail(), token);

        return Constants.REGISTERED_SUCCESSFULLY;
    }

    public String login(UserDto request) throws UsernameNotFoundException, InvalidCredentialsException{
        log.info("User login attempt for email: {}", request.getEmail());
        validateEmail(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException(Constants.INVALID_PASSWORD);
        }
        user.setLastActiveAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User login successful: {}", request.getEmail());
        // Generate Token
        String existingSession = redisService.getSession(request.getEmail());
        if (existingSession.isEmpty()){
            String token = jwtUtil.generateToken(user.getEmail());
            log.info("Generated JWT: {}", token);
            redisService.storeSession(user.getEmail(), token);
        } else {
            log.info("Exist JWT : {} ", redisService.getSession(request.getEmail()));
        }

        return Constants.LOGIN_SUCCESSFULLY;
    }

    public String logout(UserDto request){
        log.info("Logging out user: {}", request.getEmail());
        redisService.deleteSession(request.getEmail());
        return Constants.USER_LOGOUT;
    }
}