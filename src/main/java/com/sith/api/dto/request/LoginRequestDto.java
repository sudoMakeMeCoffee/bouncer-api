package com.sith.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @Email(message = "Enter a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;
}
