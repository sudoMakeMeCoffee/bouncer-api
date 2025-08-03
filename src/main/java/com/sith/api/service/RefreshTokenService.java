package com.sith.api.service;

import com.sith.api.dto.response.AuthResult;
import com.sith.api.dto.response.RefreshTokenResponseDto;
import com.sith.api.entity.RefreshToken;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenService {
    public RefreshTokenResponseDto createRefreshToken(UUID userId);
    public AuthResult generateNewAccessToken(String token);
    public List<RefreshToken> findByClientId(UUID clientId);
    void deleteAllByClientId(UUID clientId);
    public RefreshToken findByToken(String token);

}
