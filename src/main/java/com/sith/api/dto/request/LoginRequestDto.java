package com.sith.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Email(message = "Enter a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;
}
