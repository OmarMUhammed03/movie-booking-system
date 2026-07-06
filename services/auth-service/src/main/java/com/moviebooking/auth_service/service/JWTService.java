package com.moviebooking.auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String getUsernameFromToken(String token);

    String getTokenType(String token);

    boolean validateToken(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
    String hash(String token);
}