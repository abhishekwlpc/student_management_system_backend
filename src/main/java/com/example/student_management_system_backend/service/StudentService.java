package com.example.student_management_system_backend.service;

import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import com.example.student_management_system_backend.repository.StudentRepository;
import com.example.student_management_system_backend.repository.UserRepository;
import com.example.student_management_system_backend.security.TemporaryPasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest studentRequest) {

        if (userRepository.existsByUsername(studentRequest.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(studentRequest.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if(studentRepository.existsByRegistrationNumber(studentRequest.registrationNumber())) {
            throw new IllegalArgumentException("Student with this registration number already exists");
        }

        User userForStudent = new User();
        String temporaryPassword = TemporaryPasswordGenerator.generate(8);
        System.out.println("Temporary password for user " + studentRequest.username() + ": " + temporaryPassword);
        userForStudent.setMustChangePassword(true);
        // String hashedPassword = passwordEncoder.encode(temporaryPassword);
        userForStudent.setUsername(studentRequest.username());
        userForStudent.setEmail(studentRequest.email());
        userForStudent.setPasswordHash(temporaryPassword);
        userForStudent.setRole(Role.STUDENT);

        System.out.println("Password : " + userForStudent.getPasswordHash());

        User savedUser = userRepository.save(userForStudent);


        Student studentToSave = new Student();
        studentToSave.setUser(savedUser);
        studentToSave.setRegistrationNumber(studentRequest.registrationNumber());
        studentToSave.setFirstName(studentRequest.firstName());
        studentToSave.setLastName(studentRequest.lastName());
        studentToSave.setBirthDate(studentRequest.birthDate());
        studentToSave.setAddress(studentRequest.address());
        studentToSave.setGuardianName(studentRequest.guardianName());
        studentToSave.setGuardianRelationship(studentRequest.guardianRelationship());
        studentToSave.setGuardianTelephone(studentRequest.guardianTelephone());

        Student savedStudent = studentRepository.save(studentToSave);


        return new StudentResponse(
                savedStudent.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedStudent.getRegistrationNumber(),
                savedStudent.getFirstName(),
                savedStudent.getLastName(),
                savedStudent.getBirthDate(),
                savedStudent.getAddress(),
                savedStudent.getGuardianName(),
                savedStudent.getGuardianRelationship(),
                savedStudent.getGuardianTelephone()
        );
    }
}
