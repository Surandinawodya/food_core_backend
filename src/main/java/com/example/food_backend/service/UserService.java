package com.example.food_backend.service;

import com.example.food_backend.model.User;
import com.example.food_backend.repository.UserRepository;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(User user) {
        logger.info("Registering new user with email: {}", user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with id: {}", savedUser.getId());
        return savedUser;
    }

     public boolean emailExists(String email) {
        logger.debug("Checking if email exists: {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> login(String email, String password, String role) {
        logger.info("Login attempt for email: {}, role: {}", email, role);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword() != null &&
                encoder.matches(password, user.getPassword()) &&
                user.getRole() != null &&
                user.getRole().equals(role)) {
                logger.info("Login successful for user id: {}", user.getId());
                return userOpt;
            }
        }
        logger.warn("Login failed for email: {}", email);
        return Optional.empty();
    }
}


