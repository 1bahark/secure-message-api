package com.gulbahar.secure_message_api.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "test-secret-key-test-secret-key-test-secret-key-test-secret-key-1234");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generatedToken_containsUsername() {
        String token = jwtService.generateToken("bahar");

        assertEquals("bahar", jwtService.extractUsername(token));
    }

    @Test
    void tamperedToken_isRejected() {
        String token = jwtService.generateToken("bahar");
        String tampered = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(tampered));
    }

    @Test
    void expiredToken_isRejected() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken("bahar");

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }
}