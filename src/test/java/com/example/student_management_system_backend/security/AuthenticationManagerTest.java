package com.example.student_management_system_backend.security;

import com.example.student_management_system_backend.config.PasswordConfig;
import com.example.student_management_system_backend.config.SecurityConfig;
import com.example.student_management_system_backend.entity.User;
import com.example.student_management_system_backend.enums.Role;
import com.example.student_management_system_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        PasswordConfig.class,
        CustomUserDetailsService.class,
        AuthenticationManagerTest.AuthTestConfig.class
})
public class AuthenticationManagerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @TestConfiguration
    static class AuthTestConfig {
        @Bean
        DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder){
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder);

            return provider;
        }

        @Bean
        AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider){
            return new ProviderManager(daoAuthenticationProvider);
        }
    }

    @Test
    public void testToCheckCorrectCredentials(){

        // Arrange
        User user = new User();
        user.setUsername("testuserwithcorrectcredentials");
        user.setEmail("testuserwithcorrectcredentials@gmail.com");
        String password = "testuserwithcorrectcredentialsPassword";
        String encodedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(encodedPassword);
        user.setRole(Role.ROLE_STUDENT);

        userRepository.saveAndFlush(user);
        entityManager.clear();

        Authentication request = new UsernamePasswordAuthenticationToken(user.getUsername(), password);

        // Act
        Authentication result = authenticationManager.authenticate(request);

        // Assert
        assertTrue(result.isAuthenticated());
        assertEquals("testuserwithcorrectcredentials", result.getName());


    }

    @Test
    public void testToCheckIncorrectCredentials(){

        // Arrange
        User user = new User();
        user.setUsername("testuserwithincorrectcredentials");
        user.setEmail("testuserwithincorrectcredentials@gmail.com");
        String password = "testuserwithincorrectcredentialsPassword";
        String encodedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(encodedPassword);
        user.setRole(Role.ROLE_STUDENT);

        userRepository.saveAndFlush(user);
        entityManager.clear();
        String wrongPassword = "incorrectPassword";

        Authentication request = new UsernamePasswordAuthenticationToken(user.getUsername(), wrongPassword);

        // Act and Assert
        assertThrowsExactly(
                BadCredentialsException.class,
                () -> authenticationManager.authenticate(request)
        );
    }

    @Test
    public void testToCheckDoesNotExistUser(){

        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        String password = "testuserPassword";
        String encodedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(encodedPassword);
        user.setRole(Role.ROLE_STUDENT);

        userRepository.saveAndFlush(user);
        entityManager.clear();

        Authentication request = new UsernamePasswordAuthenticationToken("userDoesNotExist", password);

        // Act and Assert
        assertThrowsExactly(
                BadCredentialsException.class,
                () -> authenticationManager.authenticate(request)
        );
        assertThrowsExactly(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("userDoesNotExist")
        );

    }


}
