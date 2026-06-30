package com.moviebooking.auth_service.service.impl;

import com.moviebooking.auth_service.dto.AuthResponse;
import com.moviebooking.auth_service.dto.LoginRequest;
import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.exception.InvalidTokenException;
import com.moviebooking.auth_service.model.AuthUser;
import com.moviebooking.auth_service.model.RefreashToken;
import com.moviebooking.auth_service.model.TokenType;
import com.moviebooking.auth_service.repository.AuthUserRepository;
import com.moviebooking.auth_service.repository.TokenRepository;
import com.moviebooking.auth_service.service.AuthService;
import com.moviebooking.auth_service.service.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthUserRepository authUserRepository;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTService jwtService, TokenRepository tokenRepository, AuthUserRepository authUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authUserRepository = authUserRepository;
    }


    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            log.info("Authentication attempt for {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            AuthUser user = (AuthUser) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            persistRefreshToken(refreshToken, (AuthUser) authentication.getPrincipal());

            log.info("Authentication successful for {}", user.getEmail());

            return new AuthResponse(accessToken, refreshToken);

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for {}", request.getEmail());
            throw ex;
        }
    }

    @Override
    public void logout(String refreshToken) {
        log.info("Log out attempt for token {}",  jwtService.hash(refreshToken));
        tokenRepository.revokeByTokenHash(jwtService.hash(refreshToken));
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (jwtService.validateToken(refreshToken)
                && TokenType.REFRESH.name().equals(jwtService.getTokenType(refreshToken))) {
            RefreashToken stored = tokenRepository.findByTokenHash(jwtService.hash(refreshToken))
                    .orElseThrow(InvalidTokenException::new);

            if (stored.isRevoked()) {
                throw new InvalidTokenException();
            }
            AuthUser user = stored.getUser();

            String accessToken = jwtService.generateAccessToken(user);
            log.info("Refresh Token Updated for user with email: {}", user.getEmail());

            return new AuthResponse(accessToken, refreshToken);
        }
        log.info("Updating Refresh Token Failed for Token: {}", refreshToken);
        throw new InvalidTokenException();
    }

    @Override
    public void Register(SignUpRequest request) {
        log.info("Creating user with email: {}", request.getEmail());


    }

    private void persistRefreshToken(String refreshToken, AuthUser user) {
        Instant now = Instant.now();
        tokenRepository.save(
                RefreashToken.builder()
                        .tokenHash(jwtService.hash(refreshToken)) // store a hash, not the raw token
                        .user(user)
                        .createdAt(now)
                        .expiresAt(now.plusMillis(refreshTokenExpiration))
                        .revoked(false)
                        .build()
        );
    }


}
