package com.example.student_management_system_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.crypto.SecretKey;
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

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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
                .verifyWith(getSigningKey())
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
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expirationTime = new Date(now.getTime() + expiration);

        long actualLifeTime = expirationTime.getTime()- claims.getIssuedAt().getTime();
        long expectedLifetime = 15 * 60 * 1000L;
        long allowedDifference = 5000L;

        // Assert
        assertNotNull(claims.getIssuedAt());
        assertTrue((actualLifeTime - expectedLifetime) <= allowedDifference, "Token lifetime should be 15 minutes");
    }

    @Test
    public void testValidToken(){
        // Arrange
        String token = jwtService.generateJWTToken("testuserValidToken");

        // Act
        String username = jwtService.extractUsernameFromToken(token);

        // Assert
        assertNotNull(username);
        assertEquals("testuserValidToken",username);
    }

    @Test
    public void testExpiredToken(){
        // Arrange
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() -100000);

        String token= Jwts.builder()
                .subject("testerExpiredToken")
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();

        // Act & Assert
        assertThrowsExactly(ExpiredJwtException.class, () -> jwtService.extractUsernameFromToken(token));
    }

    @Test
    public void testUseDifferentSignedKey(){
        // Arrange
        String token = jwtService.generateJWTToken("testerWithDifferentSignedKey");

        byte[] keyBytes = Decoders.BASE64.decode("sdfsfdsfewfqkaufiqe7cqaiuhqwikxdwiddyagidyagdqwidwqduwbdac");
        SecretKey wrongSecreter =Keys.hmacShaKeyFor(keyBytes);

        // Act & Assert
        assertThrowsExactly(SignatureException.class, () -> Jwts.parser()
                .verifyWith(wrongSecreter)
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

    @Test
    public void testMalformedToken(){
        // Arrange
        String token = "asdsada-efsdfds-sfdsfsdf-sdfdsf";

        // Act & Assert
        assertThrowsExactly(MalformedJwtException.class, () -> jwtService.extractUsernameFromToken(token));

    }
}
