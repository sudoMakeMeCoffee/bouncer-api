package com.sith.api.service;

import com.sith.api.dto.response.RefreshTokenResponseDto;

import java.util.UUID;

public interface RefreshTokenService {
    public RefreshTokenResponseDto createRefreshToken(UUID userId);
}
