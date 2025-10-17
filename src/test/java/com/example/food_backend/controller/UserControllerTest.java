package com.example.food_backend.controller;

import com.example.food_backend.model.User;
import com.example.food_backend.service.JwtService;
import com.example.food_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() {
        // Prepare a user with valid password length
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("validPass"); // 9 chars, >=6
        user.setName("Test User");
        user.setRole("USER");

        // Mock email already exists
        when(userService.emailExists(user.getEmail())).thenReturn(true);

        // Call register
        ResponseEntity<?> response = userController.register(user);

        // Assert response
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email is already registered", response.getBody());
    }

    @Test
    void register_WithShortPassword_ShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("123"); // too short
        user.setName("Test User");
        user.setRole("USER");

        ResponseEntity<?> response = userController.register(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Password must be at least 6 characters", response.getBody());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "wrong@example.com");
        loginData.put("password", "wrongpass");
        loginData.put("role", "USER");

        when(userService.login(anyString(), anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.login(loginData);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email, password, or role", response.getBody());
    }

    @Test
    void login_WithValidUser_ShouldReturnToken() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setName("John");
        user.setRole("USER");

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "user@example.com");
        loginData.put("password", "userpass");
        loginData.put("role", "USER");

        when(userService.login(anyString(), anyString(), anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token123");

        ResponseEntity<?> response = userController.login(loginData);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(user, body.get("user"));
        assertEquals("token123", body.get("token"));
    }
}
