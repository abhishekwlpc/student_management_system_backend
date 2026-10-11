package com.example.student_management_system_backend.controller;

import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/student")
    public String createStudent(@RequestBody @Valid StudentRequest studentRequest) {

        studentService.createStudent(studentRequest);
        return "Student created successfully!";
    }

    @GetMapping("/example")
    public String exampleEndpoint() {
        return "This is an example endpoint.";
    }

}
