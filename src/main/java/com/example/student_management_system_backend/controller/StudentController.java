package com.example.student_management_system_backend.controller;

import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.service.StudentService;
import jakarta.validation.Valid;
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
    public StudentResponse getStudentById(@PathVariable Integer id) {
        return studentService.getStudentById(id);
    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
    }

}
