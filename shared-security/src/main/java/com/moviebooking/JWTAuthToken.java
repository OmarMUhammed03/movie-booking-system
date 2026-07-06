package com.moviebooking;


import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JWTAuthToken extends AbstractAuthenticationToken {

    private final String token;
    private final String email;
    @Getter
    private final String userId;

    // unauthenticated — created by filter
    public JWTAuthToken(String token) {
        super(null);
        this.token = token;
        this.email = null;
        this.userId = null;
        setAuthenticated(false);
    }

    // authenticated — created by provider
    public JWTAuthToken(String token, String email, String userId,
                        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.email = email;
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }


}
