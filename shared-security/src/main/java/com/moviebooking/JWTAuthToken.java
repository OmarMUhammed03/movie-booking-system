package com.moviebooking;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JWTAuthToken extends AbstractAuthenticationToken {

    private final String token;
    private final String email;

    // unauthenticated — created by filter
    public JWTAuthToken(String token) {
        super(null);
        this.token = token;
        this.email = null;
        setAuthenticated(false);
    }

    // authenticated — created by provider
    public JWTAuthToken(String token, String email,
                        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.email = email;
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
