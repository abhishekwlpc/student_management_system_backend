package com.example.student_management_system_backend.service;

import com.example.student_management_system_backend.dto.request.LoginRequest;
import com.example.student_management_system_backend.dto.response.LoginResponse;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.repository.UserRepository;
import com.example.student_management_system_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest){

        Authentication request = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication response = authenticationManager.authenticate(request);
        response.isAuthenticated();

        User user = userRepository.findByUsername(loginRequest.username())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Error occurred")
                    );
        String generatedJWTToken = jwtService.generateJWTToken(user.getUsername());


        return new LoginResponse(
                generatedJWTToken,
                "Bearer",
                user.getUsername(),
                user.getRole(),
                user.isMustChangePassword()
        );
    }


}
