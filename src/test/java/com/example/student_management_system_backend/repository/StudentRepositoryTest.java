package com.example.student_management_system_backend.repository;

import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testSaveStudent() {

        // Arrange
        User user = new User();
        user.setUsername("abhishek");
        user.setEmail("abhishek@example.com");
        user.setPasswordHash("password");
        user.setRole(Role.ROLE_STUDENT);

        User savedUser = userRepository.saveAndFlush(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegistrationNumber("123456A");
        student.setFirstName("Abhishek");
        student.setLastName("Liyanage");
        student.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student.setAddress("Galle Road, Colombo");
        student.setGuardianName("Anura Liyanage");
        student.setGuardianRelationship("Father");
        student.setGuardianTelephone("0771234567");

        // Act
        Student savedStudent = studentRepository.saveAndFlush(student);

        // Assert
        assertEquals(savedUser.getId(), savedStudent.getUser().getId());
        assertTrue(savedStudent.getId() > 0);
        assertEquals("123456A", savedStudent.getRegistrationNumber());
        assertEquals("Abhishek", savedStudent.getFirstName());
        assertEquals("Liyanage", savedStudent.getLastName());
        assertEquals(java.time.LocalDate.of(2000, 1, 1), savedStudent.getBirthDate());
        assertEquals("Galle Road, Colombo", savedStudent.getAddress());
        assertEquals("Anura Liyanage", savedStudent.getGuardianName());
        assertEquals("Father", savedStudent.getGuardianRelationship());
        assertEquals("0771234567", savedStudent.getGuardianTelephone());
        assertNotNull(savedStudent.getUser());
    }

    @Test
    public void testFindStudentById() {
        // Arrange
        User user = new User();
        user.setUsername("abhishek");
        user.setEmail("abhishek@example.com");
        user.setPasswordHash("password");
        user.setRole(Role.ROLE_STUDENT);

        User savedUser = userRepository.saveAndFlush(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegistrationNumber("123456A");
        student.setFirstName("Abhishek");
        student.setLastName("Liyanage");
        student.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student.setAddress("Galle Road, Colombo");
        student.setGuardianName("Anura Liyanage");
        student.setGuardianRelationship("Father");
        student.setGuardianTelephone("0771234567");

        // Act
        Student savedStudent = studentRepository.saveAndFlush(student);

        entityManager.clear();

        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElse(null);

        // Assert
        assertNotNull(foundStudent);
        assertEquals(savedStudent.getId(), foundStudent.getId());
        assertEquals(savedStudent.getRegistrationNumber(), foundStudent.getRegistrationNumber());
        assertEquals(savedStudent.getFirstName(), foundStudent.getFirstName());
        assertEquals(savedStudent.getLastName(), foundStudent.getLastName());
        assertEquals(savedStudent.getBirthDate(), foundStudent.getBirthDate());
        assertEquals(savedStudent.getAddress(), foundStudent.getAddress());
        assertEquals(savedStudent.getGuardianName(), foundStudent.getGuardianName());
        assertEquals(savedStudent.getGuardianRelationship(), foundStudent.getGuardianRelationship());
        assertEquals(savedStudent.getGuardianTelephone(), foundStudent.getGuardianTelephone());
    }

    @Test
    public void testVerifyStudentRole() {
        // Arrange
        User user = new User();
        user.setUsername("abhishek");
        user.setEmail("abhishek@example.com");
        user.setPasswordHash("password");
        user.setRole(Role.ROLE_STUDENT);

        User savedUser = userRepository.saveAndFlush(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegistrationNumber("123456A");
        student.setFirstName("Abhishek");
        student.setLastName("Liyanage");
        student.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student.setAddress("Galle Road, Colombo");
        student.setGuardianName("Anura Liyanage");
        student.setGuardianRelationship("Father");
        student.setGuardianTelephone("0771234567");

        // Act
        Student savedStudent = studentRepository.saveAndFlush(student);

        entityManager.clear();

        Student foundStudent = studentRepository
                .findById(savedStudent.getId())
                .orElse(null);

        // Assert
        assertNotNull(foundStudent);
        assertNotNull(foundStudent.getUser());
        assertEquals(Role.ROLE_STUDENT, foundStudent.getUser().getRole());
    }

}
