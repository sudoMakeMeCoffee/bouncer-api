package com.sith.api.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public class ClientAppResponseDto {
    private UUID id;
    private ClientResponseDto client;
    private String name;
    private String apiKey;
}
