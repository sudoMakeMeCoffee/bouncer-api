package com.sith.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResult {
    private String accessToken;
    private String refreshToken;
    private ClientResponseDto clientResponseDto;
}
