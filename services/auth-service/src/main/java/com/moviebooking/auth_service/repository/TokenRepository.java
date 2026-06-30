package com.moviebooking.auth_service.repository;

import com.moviebooking.auth_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Query("select t from RefreshToken t join fetch t.user where t.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHash(@Param("tokenHash") String tokenHash);


    @Modifying
    @Transactional
    @Query("update RefreshToken t set t.revoked = true where t.tokenHash = :tokenHash")
    int revokeByTokenHash(@Param("tokenHash") String tokenHash);
}
