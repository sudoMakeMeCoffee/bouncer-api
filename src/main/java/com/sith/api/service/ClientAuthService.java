package com.sith.api.service;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import jakarta.servlet.http.HttpServletRequest;

public interface ClientAuthService {
    public ClientResponseDto signUp(SignUpRequestDto requestDto);
    public LoginResult login(LoginRequestDto requestDto);
    public void sendVerificationLink(String to, String subject);
    public ClientResponseDto checkAuth(HttpServletRequest request);
}
