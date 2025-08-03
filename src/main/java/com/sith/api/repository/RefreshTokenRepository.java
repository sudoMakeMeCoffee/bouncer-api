package com.sith.api.repository;

import com.sith.api.entity.RefreshToken;
import com.sith.api.service.RefreshTokenService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
}
