package com.project.digitalidentityvault.filter;

import com.project.digitalidentityvault.service.RedisService;
import com.project.digitalidentityvault.util.JwtUtil;
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
import java.util.Date;
import java.util.Map;
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
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replace("Bearer ", "");
            try {
                Map<String, Object> payload = jwtUtil.extractAllClaims(token);
                email = (String) payload.get("sub");
                Date expiration = (Date) payload.get("exp");

                log.info("Extracted Email: {}", email);
                log.info("Expiration: {}", expiration);
            } catch (Exception e) {
                log.error("Invalid JWT Token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        }

        if (email != null) {
            Optional<String> session = redisService.getSession(email);
            if (session.isPresent() && session.equals(token)) {
                log.info("Valid session found for email: {}", email);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList())
                );
            } else {
                log.warn("Invalid session or JWT mismatch for email: {}", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Session or JWT Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
