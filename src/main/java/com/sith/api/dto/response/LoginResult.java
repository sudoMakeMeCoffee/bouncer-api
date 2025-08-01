package com.sith.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class LoginResult {
    private String accessToken;
    private String refreshToken;
    private ClientResponseDto clientResponseDto;
}
