package com.example.student_management_system_backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService){
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try {
            String header = request.getHeader("Authorization");
            if (!Objects.isNull(header)) {
                boolean isExist = header.regionMatches(true, 0, "Bearer ", 0, 7);
                if (isExist) {
                    String token = header.replaceFirst("(?i)^Bearer\\s+", "").trim();
                    if (!token.isEmpty()) {
                        String username = jwtService.extractUsernameFromToken(token);

                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails user = customUserDetailsService.loadUserByUsername(username);


                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                            SecurityContextHolder.getContext().setAuthentication(authentication);

                        }

                    }
                }
            }


        } catch (JwtException exception) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            return;




        }
        filterChain.doFilter(request, response);
    }
}

