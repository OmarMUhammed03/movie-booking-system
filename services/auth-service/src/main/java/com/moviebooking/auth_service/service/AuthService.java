package com.moviebooking.auth_service.service;

import com.moviebooking.auth_service.dto.AuthResponse;
import com.moviebooking.auth_service.dto.LoginRequest;
import com.moviebooking.auth_service.dto.SignUpRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    void logout(String refreshToken);

    AuthResponse refresh(String refreshToken);

    void creatUser(SignUpRequest user);

}
