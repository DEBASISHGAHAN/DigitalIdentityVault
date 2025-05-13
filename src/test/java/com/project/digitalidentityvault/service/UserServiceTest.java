package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.dto.UserDto;
import com.project.digitalidentityvault.entity.User;
import com.project.digitalidentityvault.exception.InvalidCredentialsException;
import com.project.digitalidentityvault.exception.UserAlreadyExistsException;
import com.project.digitalidentityvault.repository.UserRepository;
import com.project.digitalidentityvault.util.Constants;
import com.project.digitalidentityvault.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private MailService mailService;
    @Mock
    private RedisService redisService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OtpRateLimiterService otpRateLimiterService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserService(userRepository, otpService, mailService,
                redisService, jwtUtil, passwordEncoder, otpRateLimiterService);
    }

    @Test
    void shouldSendOtpToNewUser() {
        UserDto request = new UserDto("test@gmail.com", "password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(otpRateLimiterService.isOtpRequestAllowed(anyString())).thenReturn(true);
        when(otpService.generateOtp(anyString())).thenReturn("12345");

        String result = userService.sendOtpToNewUser(request);

        assertEquals(Constants.SEND_OTP, result);
        verify(mailService).sendOtpMail(anyString(), eq("OTP Verification"), anyString());
    }

    @Test
    void shouldThrowExceptionIfUserAlreadyExists() {
        UserDto request = new UserDto("test@gmail.com", "password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.sendOtpToNewUser(request));
    }

    @Test
    void shouldVerifyOtpSuccessfully() {
        UserDto request = new UserDto("test@gmail.com", "12345");

        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(anyString())).thenReturn("jwtToken");

        String result = userService.verifyOtp(request);

        assertEquals(Constants.REGISTERED_SUCCESSFULLY, result);
        verify(redisService).storeSession(anyString(), anyString());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        UserDto request = new UserDto("test@gmail.com", "password");
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwtToken");

        String result = userService.login(request);

        assertEquals(Constants.LOGIN_SUCCESSFULLY, result);
        verify(redisService).storeSession(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionIfPasswordIsIncorrect() {
        UserDto request = new UserDto("test@gmail.com", "wrongPassword");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        UserDto request = new UserDto("test@gmail.com", "password");

        userService.logout(request);

        verify(redisService).deleteSession(request.getEmail());
    }
}
