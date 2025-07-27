package com.sith.api.dto.request;

import jakarta.validation.constraints.*;

public class LoginRequestDto {

    @Email(message = "Enter a valid email.")
    private String email;

    @Size(min = 8, message = "Password must have minimum 8 characters.")
    private String password;
}
