package com.moviebooking;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JWTService {

    private final RSAPublicKey publicKey;

    public JWTService(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }


    public String getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false; // don't throw — callers expect a boolean
        }
    }

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

    public List<GrantedAuthority> getAuthorities(String token) {
        Claims claims = parseClaims(token);
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
    }
}