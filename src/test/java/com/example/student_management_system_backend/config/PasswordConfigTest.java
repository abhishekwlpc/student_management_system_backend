package com.example.student_management_system_backend.config;

import com.example.student_management_system_backend.security.TemporaryPasswordGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@Import({PasswordConfig.class, TemporaryPasswordGenerator.class})
@DataJpaTest
public class PasswordConfigTest {

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Test
    public void testProvideDifferentValueFromOriginalPassword(){

        // Arrange
        String originalPassword = "MySecurePassword123!";

        // Act
        String encodedPassword = passwordEncoder.encode(originalPassword);

        // Assert
        assertNotNull(originalPassword);
        assertNotNull(encodedPassword);
        assertNotEquals(originalPassword, encodedPassword);

    }

    @Test
    public void testPasswordMatchesForCorrectPassword(){

        // Arrange
        String originalPassword = "MySecurePassword123!";

        // Act
        String encodedPassword = passwordEncoder.encode(originalPassword);
        Boolean isPasswordCorrectedWithEncodedValue = passwordEncoder.matches(originalPassword, encodedPassword);

        // Assert
        assertEquals(true, isPasswordCorrectedWithEncodedValue);

    }

    @Test
    public void testPasswordMatchesForIncorrectPassword(){

        // Arrange
        String originalPassword = "MySecurePassword123!";

        // Act
        String encodedPassword = passwordEncoder.encode(originalPassword);
        Boolean isPasswordCorrectedWithEncodedValue = passwordEncoder.matches("WrongPassword", encodedPassword);

        // Assert
        assertEquals(false, isPasswordCorrectedWithEncodedValue);

    }

}
