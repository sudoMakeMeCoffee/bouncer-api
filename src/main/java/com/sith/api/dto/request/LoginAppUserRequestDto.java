package com.sith.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginAppUserRequestDto {
    @Email(message = "Enter a valid email.")
    private String email;

    @Min(value = 8, message = "Password must have minimum 8 characters.")
    private String password;
}
