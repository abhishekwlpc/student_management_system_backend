package com.example.student_management_system_backend.dto.request;


import jakarta.validation.constraints.*;

public record StudentRequest(

    @NotBlank(message = "Username is required")
    @Size(max=100, message = "Username must not exceed 100 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Registration number is required")
    @Size(min = 5, max = 50, message = "Registration number must be between 5 and 20 characters")
    String registrationNumber,

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    java.time.LocalDate birthDate,

    @NotNull(message = "Address cannot be null")
    String address,
    String guardianName,
    String guardianRelationship,
    String guardianTelephone
) {}
