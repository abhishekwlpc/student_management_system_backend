package com.example.student_management_system_backend.controller;

import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    private final StudentService studentService;


    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/student")
    public StudentResponse createStudent(@RequestBody @Valid StudentRequest studentRequest) {
        return studentService.createStudent(studentRequest);
    }

    @GetMapping("/students")
    public List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PutMapping("/student/{id}")
    public StudentResponse editStudent(@PathVariable Integer id, @RequestBody @Valid StudentRequest studentRequest) {
        return studentService.editStudent(id, studentRequest);
    }

    @GetMapping("/student/{id}")
    public StudentResponse getStudentById(@PathVariable Integer id, Authentication authentication) {
        return studentService.getStudentById(id, authentication);
    }

    @GetMapping("/student/me")
    public StudentResponse getOwnStudent(Authentication authentication){
        String username = authentication.getName();
        return studentService.getOwnStudentDetails(username);
    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
    }

}
