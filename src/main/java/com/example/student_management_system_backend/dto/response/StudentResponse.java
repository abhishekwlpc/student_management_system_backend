package com.example.student_management_system_backend.dto.response;

public record StudentResponse(
        int id,
        String username,
        String email,
        String registrationNumber,
        String firstName,
        String lastName,
        java.time.LocalDate birthDate,
        String address,
        String guardianName,
        String guardianRelationship,
        String guardianTelephone
) {}
