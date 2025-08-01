package com.sith.api.service.impl;

import com.sith.api.dto.response.RefreshTokenResponseDto;
import com.sith.api.entity.RefreshToken;
import com.sith.api.repository.RefreshTokenRepository;
import com.sith.api.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshTokenResponseDto createRefreshToken(UUID userId) {

        RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        RefreshToken createdRefreshToken = refreshTokenRepository.save(newRefreshToken);

        return RefreshTokenResponseDto.fromEntity(createdRefreshToken);
    }
}
