package com.example.student_management_system_backend.service;

import com.example.student_management_system_backend.config.PasswordConfig;
import com.example.student_management_system_backend.dto.request.StudentRequest;
import com.example.student_management_system_backend.dto.response.StudentResponse;
import com.example.student_management_system_backend.entity.Student;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import com.example.student_management_system_backend.exception.customException.AlreadyExistsException;
import com.example.student_management_system_backend.repository.StudentRepository;
import com.example.student_management_system_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import({StudentService.class, PasswordConfig.class})
@SpringBootTest
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreateStudent() {

        // Arrange
        User user = new User();
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
                "testuserd",
                "testuserd@example.com",
                "123456d",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );
        StudentRequest studentRequestWithSameUsername = new StudentRequest(
                "testuserd",
                "testusere@example.com",
                "123456e",
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
        assertThrowsExactly(AlreadyExistsException.class, () -> {
            studentService.createStudent(studentRequestWithSameUsername);
        });
    }

    @Test
    public void testRejectsDuplicateEmail() {
        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuserduplicate1",
                "testuser812@example.com",
                "123451A",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );
        StudentRequest studentRequestWithSameEmail = new StudentRequest(
                "testuserduplicate21",
                "testuser812@example.com",
                "123452B",
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
        assertThrowsExactly(AlreadyExistsException.class, () -> {
            studentService.createStudent(studentRequestWithSameEmail);
        });

        User user = userRepository.findByEmail("testuser812@example.com");
        assertNotEquals("testuserduplicate21" , user.getUsername());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testRollsBackUserWhenStudentCreationFails() {

        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuserRollback",
                "testuser44@example.com",
                "123856C",
                "Test",
                "User",
                LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );

        // Act & Assert
        doAnswer(invocation -> {
            throw new IllegalStateException("Simulated failure");
        }).when(studentRepository).save(any(Student.class));

        InvalidDataAccessApiUsageException exception =
                assertThrows(
                        InvalidDataAccessApiUsageException.class,
                        () -> studentService.createStudent(studentRequest)
                );

        assertInstanceOf(
                IllegalStateException.class,
                exception.getCause()
        );
        assertEquals("Simulated failure", exception.getCause().getMessage());

        entityManager.clear();

        assertNull(userRepository.findByEmail("testuser44@example.com"));
    }

    @Test
    public void testUserPasswordAndEmailUsernameStore(){

        // Arrange
        StudentRequest studentRequest = new StudentRequest(
                "testuserw",
                "testuserw@example.com",
                "123456w",
                "Test",
                "User",
                java.time.LocalDate.of(2000, 1, 1),
                "123 Test Street",
                "Test Guardian",
                "Parent",
                "0771234567"
        );

        studentService.createStudent(studentRequest);

        // Act
        entityManager.clear();
        User testUser = userRepository.findByEmail("testuserw@example.com");

        //Assert
        assertNotNull(testUser);
        assertNotNull(testUser.getPasswordHash());
        assertEquals("{bcrypt}", testUser.getPasswordHash().substring(0,8));
        assertTrue(testUser.isMustChangePassword());
        assertEquals(Role.ROLE_STUDENT, testUser.getRole());
        assertEquals("testuserw", testUser.getUsername());
        assertEquals("testuserw@example.com", testUser.getEmail());

    }

    @Test
    public void testStudentAccessForAnotherStudent(){
        // Arrange
        User user1 = new User();
        user1.setUsername("tester1");
        user1.setEmail("tester1@example.com");
        user1.setPasswordHash(passwordEncoder.encode("password1"));
        user1.setRole(Role.ROLE_STUDENT);

        User savedUser1 = userRepository.saveAndFlush(user1);

        Student student1 = new Student();
        student1.setUser(savedUser1);
        student1.setRegistrationNumber("123456K");
        student1.setFirstName("test");
        student1.setLastName("test");
        student1.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student1.setAddress("Galle Road, Colombo");
        student1.setGuardianName("test");
        student1.setGuardianRelationship("Father");
        student1.setGuardianTelephone("0771234567");

        User user2 = new User();
        user2.setUsername("tester2");
        user2.setEmail("tester2@example.com");
        user2.setPasswordHash(passwordEncoder.encode("password2"));
        user2.setRole(Role.ROLE_STUDENT);

        User savedUser2 = userRepository.saveAndFlush(user2);

        Student student2 = new Student();
        student2.setUser(savedUser2);
        student2.setRegistrationNumber("123456J");
        student2.setFirstName("Test2");
        student2.setLastName("test");
        student2.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student2.setAddress("Galle Road, Colombo");
        student2.setGuardianName("tester2");
        student2.setGuardianRelationship("Father");
        student2.setGuardianTelephone("0771234567");

        studentRepository.save(student1);
        studentRepository.save(student2);

        entityManager.clear();

        // Act
        Authentication request = new UsernamePasswordAuthenticationToken("tester1", "password1");
        Authentication response = authenticationManager.authenticate(request);

        // Assert
        assertThrowsExactly(AccessDeniedException.class, () -> studentService.getStudentById(student2.getId(), response));
    }

    @Test
    public void testStudentAccessForOwnDetails(){
        // Arrange
        User user1 = new User();
        user1.setUsername("tester3");
        user1.setEmail("tester3@example.com");
        user1.setPasswordHash(passwordEncoder.encode("password3"));
        user1.setRole(Role.ROLE_STUDENT);

        User savedUser1 = userRepository.saveAndFlush(user1);

        Student student1 = new Student();
        student1.setUser(savedUser1);
        student1.setRegistrationNumber("123456Z");
        student1.setFirstName("test");
        student1.setLastName("test");
        student1.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        student1.setAddress("Galle Road, Colombo");
        student1.setGuardianName("test");
        student1.setGuardianRelationship("Father");
        student1.setGuardianTelephone("0771234567");

        studentRepository.save(student1);


        entityManager.clear();

        // Act
        Authentication request = new UsernamePasswordAuthenticationToken("tester3", "password3");
        Authentication response = authenticationManager.authenticate(request);
        StudentResponse studentResponse = studentService.getStudentById(student1.getId(), response);

        // Assert
        assertTrue(response.isAuthenticated());
        assertNotNull(studentResponse);

    }

}
