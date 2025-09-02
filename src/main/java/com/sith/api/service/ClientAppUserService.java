package com.sith.api.service;

import com.sith.api.dto.request.CreateClientAppUserRequestDto;
import com.sith.api.dto.response.ClientAppUserResponseDto;

public interface ClientAppUserService {
    public ClientAppUserResponseDto registerAppUser(CreateClientAppUserRequestDto requestDto);
}
