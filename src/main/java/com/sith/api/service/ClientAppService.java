package com.sith.api.service;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.dto.response.ClientAppResponseDto;

import java.util.List;
import java.util.UUID;

public interface ClientAppService {
    public String createClientApp(CreateClientAppRequestDto requestDto);

    public List<ClientAppResponseDto> getAllAppsByClientId();
    public ClientAppResponseDto getAppById(UUID appId);
}

