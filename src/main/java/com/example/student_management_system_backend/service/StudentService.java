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

import java.util.List;

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

    public List<StudentResponse> getAllStudents() {

        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> new StudentResponse(
                student.getId(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getRegistrationNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDate(),
                student.getAddress(),
                student.getGuardianName(),
                student.getGuardianRelationship(),
                student.getGuardianTelephone()
        )).toList();
    }

    @Transactional
    public StudentResponse editStudent(Integer studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        User user = student.getUser();

        user.setUsername(studentRequest.username());
        user.setEmail(studentRequest.email());
        userRepository.save(user);

        student.setRegistrationNumber(studentRequest.registrationNumber());
        student.setFirstName(studentRequest.firstName());
        student.setLastName(studentRequest.lastName());
        student.setBirthDate(studentRequest.birthDate());
        student.setAddress(studentRequest.address());
        student.setGuardianName(studentRequest.guardianName());
        student.setGuardianRelationship(studentRequest.guardianRelationship());
        student.setGuardianTelephone(studentRequest.guardianTelephone());

        Student updatedStudent = studentRepository.save(student);

        return new StudentResponse(
                updatedStudent.getId(),
                updatedStudent.getUser().getUsername(),
                updatedStudent.getUser().getEmail(),
                updatedStudent.getRegistrationNumber(),
                updatedStudent.getFirstName(),
                updatedStudent.getLastName(),
                updatedStudent.getBirthDate(),
                updatedStudent.getAddress(),
                updatedStudent.getGuardianName(),
                updatedStudent.getGuardianRelationship(),
                updatedStudent.getGuardianTelephone()
        );
    }

    public StudentResponse getStudentById(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return new StudentResponse(
                student.getId(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getRegistrationNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDate(),
                student.getAddress(),
                student.getGuardianName(),
                student.getGuardianRelationship(),
                student.getGuardianTelephone()
        );
    }

    @Transactional
    public StudentResponse deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        studentRepository.delete(student);

        User user = userRepository.findById(student.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);

        return new StudentResponse(
                student.getId(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getRegistrationNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDate(),
                student.getAddress(),
                student.getGuardianName(),
                student.getGuardianRelationship(),
                student.getGuardianTelephone()
        );
    }
}
