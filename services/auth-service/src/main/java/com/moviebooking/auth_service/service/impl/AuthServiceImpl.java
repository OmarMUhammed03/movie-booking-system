package com.moviebooking.auth_service.service.impl;

import com.moviebooking.auth_service.dto.AuthResponse;
import com.moviebooking.auth_service.dto.LoginRequest;
import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.exception.EmailAlreadyExistsException;
import com.moviebooking.auth_service.exception.InvalidTokenException;
import com.moviebooking.auth_service.mapper.UserMapper;
import com.moviebooking.auth_service.model.AuthUser;
import com.moviebooking.auth_service.model.RefreshToken;
import com.moviebooking.auth_service.model.TokenType;
import com.moviebooking.auth_service.repository.AuthUserRepository;
import com.moviebooking.auth_service.repository.TokenRepository;
import com.moviebooking.auth_service.service.AuthService;
import com.moviebooking.auth_service.service.JWTService;
import com.moviebooking.auth_service.service.MessagingService;
import com.moviebooking.shared.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthUserRepository authUserRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MessagingService messagingService;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTService jwtService, TokenRepository tokenRepository, AuthUserRepository authUserRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, MessagingService messagingService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authUserRepository = authUserRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.messagingService = messagingService;
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
            RefreshToken stored = tokenRepository.findByTokenHash(jwtService.hash(refreshToken))
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
    @Transactional
    public void creatUser(SignUpRequest request) {
        if (authUserRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("email is already used");
        }
        log.info("Creating user with email: {}", request.getEmail());
        AuthUser user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        authUserRepository.save(user);
        messagingService.sendCreatUserEvent(
                UserRegisteredEvent.builder()
                        .authUserId(user.getId())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .phone(request.getPhone())
                        .build());
    }

    private void persistRefreshToken(String refreshToken, AuthUser user) {
        log.info("Persisting refresh token");
        Instant now = Instant.now();
        tokenRepository.save(
                RefreshToken.builder()
                        .tokenHash(jwtService.hash(refreshToken)) // store a hash, not the raw token
                        .user(user)
                        .createdAt(now)
                        .expiresAt(now.plusMillis(refreshTokenExpiration))
                        .revoked(false)
                        .build()
        );
    }


}
