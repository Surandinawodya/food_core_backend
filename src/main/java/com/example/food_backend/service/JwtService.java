package com.example.food_backend.service;

import com.example.food_backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Secret key must be at least 256 bits (32 bytes) for HS256
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "mySuperSecretKeyThatIsVeryLong1234567890".getBytes()
    );

    private final long expirationMs = 86400000; // 1 day

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }
}
