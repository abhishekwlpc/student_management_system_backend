package com.example.student_management_system_backend.service;

import com.example.student_management_system_backend.config.PasswordConfig;
import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.repository.StudentRepository;
import com.example.student_management_system_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import({StudentService.class, PasswordConfig.class})
@DataJpaTest
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @MockitoSpyBean
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateStudent() {

        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuser",
                "testuser@example.com",
                "123456A",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );

        // Act
        StudentResponse studentResponse = studentService.createStudent(studentRequest);
        entityManager.clear();

        // Assert
        assertNotNull(studentResponse);
        assertEquals("testuser", studentResponse.username());
        assertEquals("testuser@example.com", studentResponse.email());
        assertEquals("123456A", studentResponse.registrationNumber());
        assertEquals("Test", studentResponse.firstName());
        assertEquals("User", studentResponse.lastName());
        assertEquals(java.time.LocalDate.of(2000, 1, 1), studentResponse.birthDate());
        assertEquals("123 Test Street", studentResponse.address());
        assertEquals("Test Guardian", studentResponse.guardianName());
        assertEquals("Parent", studentResponse.guardianRelationship());
        assertEquals("0771234567", studentResponse.guardianTelephone());

    }

    @Test
    public void testRejectsDuplicateUsername(){
        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuser",
                "testuser@example.com",
                "123456A",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );
        StudentRequest studentRequestWithSameUsername = new StudentRequest(
                "testuser",
                "testuser2@example.com",
                "123456B",
                "Test2",
                "User2",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );

        // Act
        StudentResponse studentResponse = studentService.createStudent(studentRequest);
        entityManager.clear();

        // Assert
        assertNotNull(studentResponse);
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            studentService.createStudent(studentRequestWithSameUsername);
        });
    }

    @Test
    public void testRejectsDuplicateEmail() {
        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuser",
                "testuser@example.com",
                "123456A",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );
        StudentRequest studentRequestWithSameEmail = new StudentRequest(
                "testuser2",
                "testuser@example.com",
                "123456B",
                "Test3",
                "User3",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );


        // Act
        StudentResponse studentResponse = studentService.createStudent(studentRequest);
        entityManager.clear();

        // Assert
        assertNotNull(studentResponse);
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            studentService.createStudent(studentRequestWithSameEmail);
        });
        assertNull(userRepository.findByUsername("testuser2"));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testRollsBackUserWhenStudentCreationFails() {
        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuserRollback",
                "testuser4@example.com",
                "123456C",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );

        // Act
        doAnswer(invocation -> {
            entityManager.flush();

            throw new IllegalStateException("Simulated failure");
        }).when(studentRepository).save(any(Student.class));

        assertThrowsExactly(
                IllegalStateException.class,
                () -> studentService.createStudent(studentRequest)
        );

        entityManager.clear();

        // Assert
        assertNull(userRepository.findByUsername("testuserRollback"));
    }

}
