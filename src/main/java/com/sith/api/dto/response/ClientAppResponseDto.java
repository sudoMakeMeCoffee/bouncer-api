package com.sith.api.dto.response;

import com.sith.api.entity.ClientApp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ClientAppResponseDto {
    private UUID id;
    private ClientResponseDto client;
    private String name;
    private String apiKey;
    private List<ClientAppUserResponseDto> users;

    public static ClientAppResponseDto fromEntity(ClientApp clientApp){
        return ClientAppResponseDto.builder()
                .id(clientApp.getId())
                .client(ClientResponseDto.fromEntity(clientApp.getClient()))
                .name(clientApp.getName())
                .apiKey(clientApp.getApiKey())
                .users(clientApp.getUsers().stream().map(ClientAppUserResponseDto::fromEntity).toList())
                .build();
    }
}
