package com.example.student_management_system_backend.controller;

import com.example.student_management_system_backend.dto.request.LoginRequest;
import com.example.student_management_system_backend.dto.response.LoginResponse;
import com.example.student_management_system_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest){
        return  authService.login(loginRequest);
    }
}
