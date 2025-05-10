package com.project.digitalidentityvault.filter;

import com.project.digitalidentityvault.service.RedisService;
import com.project.digitalidentityvault.util.Constants;
import com.project.digitalidentityvault.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        if (email == null || !jwtUtil.isTokenValid(token, email)) {
            log.warn("Invalid JWT Token");
            throw new JwtException(Constants.INVALID_SESSION);
        }

        String session = redisService.getSession(email);
        if (session.isEmpty() || !session.equals(token)) {
            log.warn("Invalid session for email: {}", email);
            throw new JwtException(Constants.INVALID_SESSION);
        }

        log.info("Authenticated user: {}", email);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList())
        );

        filterChain.doFilter(request, response);
    }
}