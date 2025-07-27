package com.sith.api.controller;

import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.service.ClientAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/client")
public class ClientAuthController {

    private final ClientAuthService clientAuthService;
    private final PasswordEncoder passwordEncoder;

    public ClientAuthController(ClientAuthService clientAuthService, PasswordEncoder passwordEncoder) {
        this.clientAuthService = clientAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<ClientResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto requestDto){
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        ClientResponseDto savedClient = clientAuthService.signUp(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Client created successfully",
                savedClient,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
