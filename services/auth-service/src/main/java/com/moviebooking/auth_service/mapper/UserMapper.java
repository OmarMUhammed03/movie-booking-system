package com.moviebooking.auth_service.mapper;

import com.moviebooking.auth_service.dto.SignUpRequest;
import com.moviebooking.auth_service.model.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AuthUser toEntity(SignUpRequest user);
}
