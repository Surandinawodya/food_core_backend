package com.example.food_backend.service;

import com.example.food_backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_ShouldIncludeUserEmailAndRole() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setRole("USER");

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        Claims claims = Jwts.parser()
                .setSigningKey("mySuperSecretKeyThatIsVeryLong1234567890".getBytes())
                .parseClaimsJws(token)
                .getBody();

        assertEquals("john@example.com", claims.getSubject());
        assertEquals("USER", claims.get("role"));
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }
}