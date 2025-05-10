package com.project.digitalidentityvault.controller;

import com.project.digitalidentityvault.dto.*;
import com.project.digitalidentityvault.exception.InvalidCredentialsException;
import com.project.digitalidentityvault.exception.UserException;
import com.project.digitalidentityvault.exception.UserAlreadyExistsException;
import com.project.digitalidentityvault.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> sendOtpToNewUser(@Valid @RequestBody UserDto request)
            throws UserAlreadyExistsException, UserException {
        return ResponseEntity.ok(userService.sendOtpToNewUser(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody UserDto request)
            throws UserException, UsernameNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.verifyOtp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDto request)
            throws UsernameNotFoundException, InvalidCredentialsException {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody UserDto request){
        return ResponseEntity.ok(userService.logout(request));
    }
}