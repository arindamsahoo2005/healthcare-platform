package com.healthcare.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenServiceTest {

    private AuthTokenService authTokenService;

    @BeforeEach
    void setUp() {
        authTokenService = new AuthTokenService();
        ReflectionTestUtils.setField(authTokenService, "secretKey", "CarePulseSecretTestKey2025!");
    }

    @Test
    void testTokenGenerationAndValidation() {
        String username = "arindam.sahoo";
        String token = authTokenService.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String extracted = authTokenService.validateAndExtractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void testTamperedTokenFails() {
        String username = "arindam.sahoo";
        String token = authTokenService.generateToken(username);

        // Tamper with the token
        String tampered = token.substring(0, token.length() - 4) + "AAAA";
        String extracted = authTokenService.validateAndExtractUsername(tampered);

        assertNull(extracted);
    }

    @Test
    void testNullOrBlankTokenReturnsNull() {
        assertNull(authTokenService.validateAndExtractUsername(null));
        assertNull(authTokenService.validateAndExtractUsername(""));
        assertNull(authTokenService.validateAndExtractUsername("   "));
    }
}
