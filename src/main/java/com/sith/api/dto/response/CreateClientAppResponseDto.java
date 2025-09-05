package com.sith.api.dto.response;

import com.sith.api.entity.ClientApp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateClientAppResponseDto {
    private UUID id;
    private ClientResponseDto client;
    private String name;
    private String apiKey;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static CreateClientAppResponseDto fromEntity(ClientApp clientApp){
        return CreateClientAppResponseDto.builder()
                .id(clientApp.getId())
                .client(ClientResponseDto.fromEntity(clientApp.getClient()))
                .name(clientApp.getName())
                .apiKey(clientApp.getApiKey())
                .updatedAt(clientApp.getUpdatedAt())
                .createdAt(clientApp.getCreatedAt())
                .build();
    }
}
