package com.jagha.collabflow.contoller;

import com.jagha.collabflow.dto.AuthResponse;
import com.jagha.collabflow.dto.LoginRequest;
import com.jagha.collabflow.dto.RegisterRequest;
import com.jagha.collabflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }
}
