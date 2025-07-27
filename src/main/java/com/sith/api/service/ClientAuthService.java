package com.sith.api.service;

import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import org.springframework.stereotype.Service;

public interface ClientAuthService {
    public ClientResponseDto signUp(SignUpRequestDto requestDto);
}
