package com.example.student_management_system_backend.security;

import com.example.student_management_system_backend.config.PasswordConfig;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import com.example.student_management_system_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({CustomUserDetailsService.class, PasswordConfig.class})
public class CustomUserDetailsServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordConfig passwordConfig;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    EntityManager entityManager;

    @Test
    public void testUserExists(){
        // Arrange
        User user = new User();
        user.setUsername("testCustomUser");
        user.setEmail("testusercustom@test.com");
        user.setRole(Role.ROLE_STUDENT);
        String password = "customuserpasowrd";
        String hashedPassword = passwordConfig.passwordEncoder().encode(password);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testCustomUser");
        boolean hasRole = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"));

        // Assert
        assertEquals("testCustomUser", userDetails.getUsername());
        assertEquals(hashedPassword, userDetails.getPassword());
        assertTrue(hasRole);
    }

    @Test
    public void testUserDoesNotExists(){

        // Arrange, Act, Assert
        assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("doesnotExistUsername")
        );
    }
}
