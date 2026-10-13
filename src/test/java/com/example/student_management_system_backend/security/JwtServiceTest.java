package com.example.student_management_system_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({JwtService.class})
public class JwtServiceTest {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired
    private JwtService jwtService;

    @Test
    public void testSigningKey(){

        // Act
        String token = jwtService.generateJWTToken("testuserForTokenTest");
        String[] parts = token.split("\\.");

        // Assert
        assertNotNull(token);
        assertFalse(parts[0].isBlank());
        assertFalse(parts[1].isBlank());
        assertFalse(parts[2].isBlank());
    }

    @Test
    public void testParseTokenWithExpectedVerificationKey(){
        // Arrange
        String token = jwtService.generateJWTToken("testuser");

        // Act
        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(jwtService.getSigningKey())
                .build()
                .parseSignedClaims(token);

        // Assert
        assertNotNull(parsedToken);
        assertEquals("testuser", parsedToken.getPayload().getSubject());
    }

    @Test
    public void testTokenIssueTimeValidation(){
        // Arrange
        String token = jwtService.generateJWTToken("testuser");

        // Act
        Date now = new Date();
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expirationTime = claims.getExpiration();

        // Assert
        assertNotNull(expirationTime);
        assertTrue(now.getTime()  < expirationTime.getTime());
    }
}
