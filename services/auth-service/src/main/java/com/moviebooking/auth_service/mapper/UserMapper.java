package com.moviebooking.auth_service.mapper;

import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.model.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AuthUser toEntity(SignUpRequest user){
        if(user == null) return null;
        AuthUser authUser = new AuthUser();
        authUser.setEmail(user.getEmail());
        authUser.setPassword(user.getPassword());
        authUser.setRole(user.getRole());
        return authUser;
    }
}
