package com.interview.task.creditcardAPI.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("super_long_secret_key_here_that_is_at_least_64_characters_long", 20000, 40000);
    }

    @Test
    void generateAccessToken_ShouldGenerateValidToken() {
        String username = "testUser";

        String token = jwtUtil.generateAccessToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidToken() {
        String username = "testUser";

        String token = jwtUtil.generateRefreshToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void validateToken_ValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateAccessToken("validUser");

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalidToken";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        String username = "testUser";
        String token = jwtUtil.generateAccessToken(username);

        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void getExpirationFromToken_ShouldReturnCorrectExpiration() {
        String username = "testUser";
        String token = jwtUtil.generateAccessToken(username);

        Instant expiration = jwtUtil.getExpirationFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.isAfter(Instant.now()));
    }
}
