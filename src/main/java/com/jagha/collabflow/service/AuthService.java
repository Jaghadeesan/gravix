package com.jagha.collabflow.service;

import com.jagha.collabflow.repository.UserRepository;
import com.jagha.collabflow.config.JwtUtil;
import com.jagha.collabflow.dto.AuthResponse;
import com.jagha.collabflow.dto.LoginRequest;
import com.jagha.collabflow.dto.RegisterRequest;
import com.jagha.collabflow.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if email exists already
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        // Generate token and return response
        String token = jwtUtil.generateToken(request.getEmail());
        return new AuthResponse(token, request.getEmail(), request.getFullName());
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password against the stored hash
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token and return response
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName());
    }
}
