package com.jagha.collabflow.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();

        //Inject values that would normally come from application.properties
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-minimum-32-characters-long!!");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    public void generateToken_ReturnsNonNullToken() {
        String token = jwtUtil.generateToken("test@example.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void extractEmail_ReturnsCorrectEmail() {
        String token = jwtUtil.generateToken("test@example.com");
        String extracted = jwtUtil.extractEmailFromToken(token);
        assertEquals("test@example.com", extracted);
    }

    @Test
    public void isTokenValid_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("test@example.com");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    public void isTokenValid_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.isTokenValid("Completely.invalid.token"));
    }

    @Test
    public void isTokenValid_TamperedToken_ReturnsFalse() {
        String token = jwtUtil.generateToken("test@example.com");
        String tampered = token + "tampered";
        assertFalse(jwtUtil.isTokenValid(tampered));
    }
}
