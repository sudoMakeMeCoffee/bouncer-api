package com.sith.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResult {
    private String token;
    private ClientResponseDto clientResponseDto;
}
