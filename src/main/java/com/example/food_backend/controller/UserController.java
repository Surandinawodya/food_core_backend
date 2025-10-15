package com.example.food_backend.controller;

import com.example.food_backend.model.User;
import com.example.food_backend.service.JwtService;
import com.example.food_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getName() == null || user.getName().isEmpty())
                return ResponseEntity.badRequest().body("Name is required");
            if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                return ResponseEntity.badRequest().body("Valid email is required");
            if (user.getPassword() == null || user.getPassword().length() < 6)
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");
            if (userService.emailExists(user.getEmail()))
                return ResponseEntity.badRequest().body("Email is already registered");

            User savedUser = userService.register(user);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error occurred");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            String role = loginData.get("role");

            if (email == null || email.isEmpty())
                return ResponseEntity.badRequest().body("Email is required");
            if (password == null || password.isEmpty())
                return ResponseEntity.badRequest().body("Password is required");
            if (role == null || role.isEmpty())
                return ResponseEntity.badRequest().body("Role is required");

            Optional<User> userOpt = userService.login(email, password, role);
            if (userOpt.isEmpty())
                return ResponseEntity.status(401).body("Invalid email, password, or role");

            User user = userOpt.get();
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(Map.of("user", user, "token", token));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error occurred");
        }
    }
}
