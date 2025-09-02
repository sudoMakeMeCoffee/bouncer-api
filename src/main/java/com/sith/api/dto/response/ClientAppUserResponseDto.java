package com.sith.api.dto.response;

import com.sith.api.entity.ClientAppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ClientAppUserResponseDto {
    private UUID id;
    private ClientAppResponseDto clientApp;
    private String email;
    private boolean emailVerified;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static ClientAppUserResponseDto fromEntity(ClientAppUser appUser) {
        return ClientAppUserResponseDto.builder()
                .id(appUser.getId())
                .clientApp(ClientAppResponseDto.fromEntity(appUser.getClientApp()))
                .email(appUser.getEmail())
                .emailVerified(appUser.isEmailVerified())
                .updatedAt(appUser.getUpdatedAt())
                .createdAt(appUser.getCreatedAt())
                .build();
    }
}
