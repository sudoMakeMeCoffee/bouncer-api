package com.sith.api.service.impl;

import com.sith.api.dto.response.AuthResult;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.RefreshTokenResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.entity.RefreshToken;
import com.sith.api.exception.UnauthorizedException;
import com.sith.api.repository.ClientRepository;
import com.sith.api.repository.RefreshTokenRepository;
import com.sith.api.service.ClientDetailsService;
import com.sith.api.service.RefreshTokenService;
import com.sith.api.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final ClientDetailsService clientDetailsService;
    private final ClientRepository clientRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil, ClientDetailsService clientDetailsService, ClientRepository clientRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.clientDetailsService = clientDetailsService;
        this.clientRepository = clientRepository;
    }

    @Override
    public RefreshTokenResponseDto createRefreshToken(UUID userId) {

        RefreshToken newRefreshToken = RefreshToken.builder()
                .clientId(userId)
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        RefreshToken createdRefreshToken = refreshTokenRepository.save(newRefreshToken);

        return RefreshTokenResponseDto.fromEntity(createdRefreshToken);
    }

    @Override
    public AuthResult generateNewAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid refresh token"));

        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        Client client = clientRepository.findById(refreshToken.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        UserDetails userDetails = clientDetailsService.loadUserByUsername(client.getEmail());
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .clientResponseDto(ClientResponseDto.fromEntity(client))
                .build();
    }

    @Override
    public List<RefreshToken> findByClientId(UUID userId) {
        return List.of();
    }

    @Transactional
    @Override
    public void deleteAllByClientId(UUID clientId) {
        refreshTokenRepository.deleteAllByClientId(clientId);
        refreshTokenRepository.flush();
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));
    }



}
