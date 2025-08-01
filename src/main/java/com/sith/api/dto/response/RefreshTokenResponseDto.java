package com.sith.api.dto.response;

import com.sith.api.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
public class RefreshTokenResponseDto {
    private UUID id;
    private UUID userId;
    private String token;
    private String ipAddress;
    private LocalDateTime expiredAt;

    public static RefreshTokenResponseDto fromEntity(RefreshToken refreshToken){
        return RefreshTokenResponseDto.builder()
                .id(refreshToken.getId())
                .userId(refreshToken.getUserId())
                .token(refreshToken.getToken())
                .expiredAt(refreshToken.getExpiredAt())
                .build();
    }
}
