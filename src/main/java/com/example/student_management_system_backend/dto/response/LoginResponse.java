package com.example.student_management_system_backend.dto.response;

import com.example.student_management_system_backend.enums.Role;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String username,
        Role role,
        boolean mustChangePassword
) {}
