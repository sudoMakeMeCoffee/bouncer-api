package com.sith.api.dto.response;

import com.sith.api.entity.Client;
import com.sith.api.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ClientResponseDto {
    private UUID id;
    private String email;
    private String username;
    private Role role;

    public static ClientResponseDto fromEntity(Client client){
        return  ClientResponseDto.builder()
                .id(client.getId())
                .email(client.getEmail())
                .username(client.getUsername())
                .role(client.getRole())
                .build();
    }
}
