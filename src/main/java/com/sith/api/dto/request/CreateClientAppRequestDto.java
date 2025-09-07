package com.sith.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientAppRequestDto {
    @NotBlank(message = "App name is required.")
    private String name;
}
