package com.moviebooking.auth_service.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.moviebooking.auth_service.dto.AuthResponse;
import com.moviebooking.auth_service.dto.GoogleLoginRequest;
import com.moviebooking.auth_service.dto.LoginRequest;
import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.exception.EmailAlreadyExistsException;
import com.moviebooking.auth_service.exception.InvalidTokenException;
import com.moviebooking.auth_service.mapper.UserMapper;
import com.moviebooking.auth_service.model.AuthUser;
import com.moviebooking.auth_service.model.RefreshToken;
import com.moviebooking.auth_service.model.Role;
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
import java.util.Optional;
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
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTService jwtService, TokenRepository tokenRepository, AuthUserRepository authUserRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, MessagingService messagingService, GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authUserRepository = authUserRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.messagingService = messagingService;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
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

            return new AuthResponse(accessToken, refreshToken, user.getId());

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for {}", request.getEmail());
            throw ex;
        }
    }


    @Override
    public void logout(String refreshToken) {
        log.info("Log out attempt for token {}", jwtService.hash(refreshToken));
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

            return new AuthResponse(accessToken, refreshToken, user.getId());
        }
        log.info("Updating Refresh Token Failed for Token: {}", refreshToken);
        throw new InvalidTokenException();
    }


    @Override
    @Transactional
    public void creatUser(SignUpRequest request) {
        if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("email is already used");
        }
        log.info("Creating user with email: {}", request.getEmail());
        AuthUser user = userMapper.toEntity(request);
        user.setRole(Role.USER);
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

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(request.idToken());
        } catch (Exception e) {
            log.warn("Google token verification threw an exception", e);
            throw new InvalidTokenException();
        }

        if (idToken == null) {
            log.warn("Google login failed: invalid ID token");
            throw new InvalidTokenException();
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            log.warn("Google login rejected: email not verified for {}", payload.getEmail());
            throw new InvalidTokenException();
        }

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        AuthUser user = authUserRepository.findByProviderId(googleId)
                .orElseGet(() -> provisionGoogleUser(googleId, email,firstName, lastName));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(refreshToken, user);

        log.info("Google authentication successful for {}", user.getEmail());

        return new AuthResponse(accessToken, refreshToken, user.getId());
    }

    private AuthUser provisionGoogleUser(String googleId, String email, String firstName, String lastName) {
        Optional<AuthUser> existing = authUserRepository.findByEmail(email);

        if (existing.isPresent()) {
            AuthUser user = existing.get();
            if (user.getProviderId() == null) {
                log.info("Linking Google identity to existing local account {}", email);
                user.setProviderId(googleId);
                return authUserRepository.save(user);
            }
            // providerId already set — should already have been caught by
            // findByProviderId lookup before this method is even called
            return user;
        }

        log.info("Creating new user via Google: {}", email);
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setProviderId(googleId);
        user.setPassword(null);
        user.setRole(Role.USER);

        AuthUser saved = authUserRepository.save(user);
        messagingService.sendCreatUserEvent(
                UserRegisteredEvent.builder()
                        .authUserId(user.getId())
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(null)
                        .build());
        return saved;
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
