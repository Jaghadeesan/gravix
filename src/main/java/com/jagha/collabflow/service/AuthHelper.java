package com.jagha.collabflow.service;

import com.jagha.collabflow.entity.User;
import com.jagha.collabflow.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

    private final UserRepository userRepository;

    public AuthHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
