package com.moviebooking.auth_service.controller;

import com.moviebooking.auth_service.dto.AuthResponse;
import com.moviebooking.auth_service.dto.LoginRequest;
import com.moviebooking.auth_service.dto.RefreshRequest;
import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.exception.InvalidTokenException;
import com.moviebooking.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid SignUpRequest request){
        authService.creatUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request){
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build(); // 204 No Content is standard for logout
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request){
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }
}