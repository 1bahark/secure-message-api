package com.gulbahar.secure_message_api.security;

import com.gulbahar.secure_message_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("FILTRE CALISTI: " + request.getRequestURI() + " | header: " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("FILTRE: Bearer header yok, kimlik atanmadi");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("FILTRE: kimlik atandi: " + username);
        } catch (Exception e) {
            System.out.println("JWT HATASI: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}