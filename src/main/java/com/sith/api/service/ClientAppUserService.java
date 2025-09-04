package com.sith.api.service;

import com.sith.api.dto.request.CreateClientAppUserRequestDto;
import com.sith.api.dto.request.RegisterAppUserRequestDto;
import com.sith.api.dto.response.ClientAppUserResponseDto;

import java.util.List;
import java.util.UUID;

public interface ClientAppUserService {
    public ClientAppUserResponseDto registerAppUser(CreateClientAppUserRequestDto requestDto);
    public List<ClientAppUserResponseDto> getAllAppUsersByAppId(UUID appId);
    public ClientAppUserResponseDto register(RegisterAppUserRequestDto requestDto, String apiKey);
}
