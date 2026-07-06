package com.moviebooking.auth_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final UUID userId;
    private final String tokenType = "Bearer";

    public AuthResponse(String accessToken, String refreshToken, UUID userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
