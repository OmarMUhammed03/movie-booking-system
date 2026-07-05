package com.moviebooking.auth_service.repository;

import com.moviebooking.auth_service.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByEmail(String username);
    Optional<AuthUser> findByProviderId(String providerId);

}
