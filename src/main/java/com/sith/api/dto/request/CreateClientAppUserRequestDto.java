package com.sith.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateClientAppUserRequestDto {

    private UUID clientAppId;

    @Email(message = "Enter a valid email.")
    private String email;

    @Min(value = 8, message = "Password must have minimum 8 characters.")
    private String password;
}
