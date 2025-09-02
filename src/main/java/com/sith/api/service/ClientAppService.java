package com.sith.api.service;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.dto.response.ClientAppResponseDto;

import java.util.List;

public interface ClientAppService {
    public String createClientApp(CreateClientAppRequestDto requestDto);

    public List<ClientAppResponseDto> getAllAppsByClientId();
}

