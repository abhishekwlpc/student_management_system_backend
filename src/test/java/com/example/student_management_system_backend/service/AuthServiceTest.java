package com.example.student_management_system_backend.service;

import com.example.student_management_system_backend.config.PasswordConfig;
import com.example.student_management_system_backend.dto.request.LoginRequest;
import com.example.student_management_system_backend.dto.response.LoginResponse;
import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import com.example.student_management_system_backend.repository.StudentRepository;
import com.example.student_management_system_backend.repository.UserRepository;
import com.example.student_management_system_backend.security.JwtService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import({AuthService.class, PasswordConfig.class})
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    @Test
    public void testSuccessfulLogin(){

        // Arrange
        User user = new User();
        user.setUsername("testusersuccessfullLogin");
        user.setEmail("testusersuccessfullLogin@example.com");
        String password = "testPasswordfortestusersuccessfullLogin";
        String hashedPassword = passwordConfig.passwordEncoder().encode(password);
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.ROLE_STUDENT);
        User savedUser = userRepository.saveAndFlush(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegistrationNumber("123456V");
        student.setFirstName("testuser");
        student.setLastName("test");
        student.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student.setAddress("Galle Road, Colombo");
        student.setGuardianName("Test");
        student.setGuardianRelationship("Father");
        student.setGuardianTelephone("0771234567");
        studentRepository.saveAndFlush(student);

        entityManager.clear();

        // Act
        LoginRequest loginRequest = new LoginRequest("testusersuccessfullLogin", "testPasswordfortestusersuccessfullLogin");
        LoginResponse loginResponse = authService.login(loginRequest);

        // Assert
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.accessToken());
        assertEquals("Bearer" , loginResponse.tokenType());
        assertEquals("testusersuccessfullLogin", loginResponse.username());
        assertEquals(Role.ROLE_STUDENT, loginResponse.role());
        assertTrue(loginResponse.mustChangePassword());

    }

    @Test
    public void testLoginWithIncorrectCredentials(){

        // Arrange
        User user = new User();
        user.setUsername("testuserincorrectcredentials");
        user.setEmail("testuserincorrectcredentials@example.com");
        String password = "testPasswordfortestuserincorrectcredentials";
        String hashedPassword = passwordConfig.passwordEncoder().encode(password);
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.ROLE_STUDENT);
        User savedUser = userRepository.saveAndFlush(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setRegistrationNumber("123456X");
        student.setFirstName("testuser");
        student.setLastName("test");
        student.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student.setAddress("Galle Road, Colombo");
        student.setGuardianName("Test");
        student.setGuardianRelationship("Father");
        student.setGuardianTelephone("0771234567");
        studentRepository.saveAndFlush(student);

        entityManager.clear();

        // Act
        LoginRequest loginRequest = new LoginRequest("testuserincorrectcredentials", "wrongPassword");

        // Assert
        assertThrowsExactly(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

}
