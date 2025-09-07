package com.sith.api.dto.request;

import com.sith.api.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    @NotBlank(message = "Username is required.")
    private String username;

    @Email(message = "Enter a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    private Role role;
}
