package com.sith.api.service;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;

public interface ClientAuthService {
    public LoginResult signUp(SignUpRequestDto requestDto);
    public LoginResult login(LoginRequestDto requestDto);
}
