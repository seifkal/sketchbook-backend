package com.sketchbook.sketchbook_backend.security;

import com.sketchbook.sketchbook_backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void generateToken_andExtractUsername() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", base64Secret());
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 60000L);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("artist");
        user.setEmail("artist@example.com");
        user.setAvatarVariant("variant");
        user.setAvatarColors(List.of("#123456"));

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo(user.getId().toString());
    }

    @Test
    void isTokenValid_returnsFalseForDifferentUser() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", base64Secret());
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 60000L);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("artist");
        user.setEmail("artist@example.com");

        String token = jwtService.generateToken(user);

        UserDetails otherUser = new org.springframework.security.core.userdetails.User(
                UUID.randomUUID().toString(),
                "password",
                List.of());

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    private String base64Secret() {
        String raw = "supersecretkeysupersecretkeysupersecretkey";
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
