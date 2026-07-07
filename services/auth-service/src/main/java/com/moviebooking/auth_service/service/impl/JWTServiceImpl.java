package com.moviebooking.auth_service.service.impl;

import com.moviebooking.auth_service.model.AuthUser;
import com.moviebooking.auth_service.model.TokenType;
import com.moviebooking.auth_service.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTServiceImpl implements JWTService {

    private final RSAPrivateKey privateKey ;

    private final RSAPublicKey publicKey ;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public JWTServiceImpl(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenExpiration, TokenType.ACCESS.name());
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        String refreshToken = buildToken(userDetails, refreshTokenExpiration, TokenType.REFRESH.name());
        return refreshToken;
    }

    private String buildToken(UserDetails userDetails, long expiration, String type) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        var now = System.currentTimeMillis();
        AuthUser authUser = (AuthUser) userDetails;
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("type", type)
                .claim("userId", authUser.getId().toString())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false; // don't throw — callers expect a boolean
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (!validateToken(token)) return false;
        return getUsernameFromToken(token).equals(userDetails.getUsername());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token", e);
        }
    }

    @Override
    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}