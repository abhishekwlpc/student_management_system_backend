package com.example.student_management_system_backend.config;

import com.example.student_management_system_backend.security.CustomUserDetailsService;
import com.example.student_management_system_backend.security.JwtAuthenticationFilter;
import com.example.student_management_system_backend.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtService jwtService, CustomUserDetailsService customUserDetailsService){
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Authentication required\"}");
                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"message\":\"Access Denied\"}");
                                })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/student"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/student/me"
                        ).hasAnyAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/students",
                                "/api/v1/student"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/student/{id}"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_STUDENT")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/student/{id}"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/student/{id}"
                        ).hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                new JwtAuthenticationFilter(
                        jwtService,
                        customUserDetailsService
                ),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("*")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService customUserDetailsService,PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider){

        return new ProviderManager(daoAuthenticationProvider);

    }
}
