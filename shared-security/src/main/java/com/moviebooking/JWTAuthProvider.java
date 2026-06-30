package com.moviebooking;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



public class JWTAuthProvider implements AuthenticationProvider {

    private final JWTService jwtService;

    public JWTAuthProvider(JWTService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        String email = jwtService.getUsernameFromToken(token);

        List<GrantedAuthority> roles = jwtService.getAuthorities(token);

        return new JWTAuthToken(token, email, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthToken.class.isAssignableFrom(authentication);
    }
}