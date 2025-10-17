package com.example.food_backend.service;

import com.example.food_backend.model.User;
import com.example.food_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User savedUser = userService.register(user);

        assertNotNull(savedUser.getPassword());
        assertTrue(encoder.matches("password123", savedUser.getPassword()));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void emailExists_WhenEmailExists_ShouldReturnTrue() {
        String email = "john@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        boolean exists = userService.emailExists(email);

        assertTrue(exists);
    }

    @Test
    void emailExists_WhenEmailDoesNotExist_ShouldReturnFalse() {
        String email = "john@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean exists = userService.emailExists(email);

        assertFalse(exists);
    }

    @Test
    void login_WithCorrectPasswordAndRole_ShouldReturnUser() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword(encoder.encode("password123"));
        user.setRole("USER");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john@example.com", "password123", "USER");

        assertTrue(result.isPresent());
        assertEquals("USER", result.get().getRole());
    }

    @Test
    void login_WithWrongPassword_ShouldReturnEmpty() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword(encoder.encode("password123"));
        user.setRole("USER");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john@example.com", "wrongpass", "USER");

        assertTrue(result.isEmpty());
    }

    @Test
    void login_WithWrongRole_ShouldReturnEmpty() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword(encoder.encode("password123"));
        user.setRole("USER");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("john@example.com", "password123", "ADMIN");

        assertTrue(result.isEmpty());
    }
}
