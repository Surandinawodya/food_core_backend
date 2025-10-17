package com.example.food_backend.controller;

import com.example.food_backend.model.User;
import com.example.food_backend.service.JwtService;
import com.example.food_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
     private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

  @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Received signup request for email: {}", user.getEmail());
        try {
            if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                logger.warn("Invalid email format: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Valid email is required");
            }

            if (userService.emailExists(user.getEmail())) {
                logger.warn("Email already registered: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Email is already registered");
            }

            if (user.getName() == null || user.getName().isEmpty()) {
                logger.warn("Name is missing for email: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Name is required");
            }

            if (user.getPassword() == null || user.getPassword().length() < 6) {
                logger.warn("Password too short for email: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");
            }

            User savedUser = userService.register(user);
            logger.info("User signup successful: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            logger.error("Error during signup for email: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body("Server error occurred");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String role = loginData.get("role");
        logger.info("Received login request for email: {}, role: {}", email, role);
        try {
            if (email == null || email.isEmpty())
                return ResponseEntity.badRequest().body("Email is required");
            if (loginData.get("password") == null || loginData.get("password").isEmpty())
                return ResponseEntity.badRequest().body("Password is required");
            if (role == null || role.isEmpty())
                return ResponseEntity.badRequest().body("Role is required");

            Optional<User> userOpt = userService.login(email, loginData.get("password"), role);
            if (userOpt.isEmpty()) {
                logger.warn("Invalid login attempt for email: {}", email);
                return ResponseEntity.status(401).body("Invalid email, password, or role");
            }

            User user = userOpt.get();
            String token = jwtService.generateToken(user);
            logger.info("Login successful for user id: {}", user.getId());
            return ResponseEntity.ok(Map.of("user", user, "token", token));

        } catch (Exception e) {
            logger.error("Error during login for email: {}", email, e);
            return ResponseEntity.status(500).body("Server error occurred");
        }
    }
}
